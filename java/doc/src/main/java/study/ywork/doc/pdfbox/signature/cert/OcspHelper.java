package study.ywork.doc.pdfbox.signature.cert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import study.ywork.doc.pdfbox.signature.SigUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;

public class OcspHelper {
    private static final Log LOG = LogFactory.getLog(OcspHelper.class);
    private final X509Certificate issuerCertificate;
    private final Date signDate;
    private final X509Certificate certificateToCheck;
    private final Set<X509Certificate> additionalCerts;
    private final String ocspUrl;
    private DEROctetString encodedNonce;
    private X509Certificate ocspResponderCertificate;
    private final JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
    private static final Random RANDOM = new SecureRandom();

    public OcspHelper(X509Certificate checkCertificate, Date signDate, X509Certificate issuerCertificate,
                      Set<X509Certificate> additionalCerts, String ocspUrl) {
        this.certificateToCheck = checkCertificate;
        this.signDate = signDate;
        this.issuerCertificate = issuerCertificate;
        this.additionalCerts = additionalCerts;
        this.ocspUrl = ocspUrl;
    }

    X509Certificate getCertificateToCheck() {
        return certificateToCheck;
    }

    public OCSPResp getResponseOcsp()
            throws IOException, OCSPException, RevokedCertificateException, URISyntaxException {
        OCSPResp ocspResponse = performRequest(ocspUrl);
        verifyOcspResponse(ocspResponse);
        return ocspResponse;
    }

    public X509Certificate getOcspResponderCertificate() {
        return ocspResponderCertificate;
    }

    private void verifyOcspResponse(OCSPResp ocspResponse)
            throws OCSPException, RevokedCertificateException, IOException {
        verifyRespStatus(ocspResponse);

        BasicOCSPResp basicResponse = (BasicOCSPResp) ocspResponse.getResponseObject();
        if (basicResponse != null) {
            ResponderID responderID = basicResponse.getResponderId().toASN1Primitive();
            X500Name name = responderID.getName();

            if (name != null) {
                findResponderCertificateByName(basicResponse, name);
            } else {
                byte[] keyHash = responderID.getKeyHash();
                if (keyHash != null) {
                    findResponderCertificateByKeyHash(basicResponse, keyHash);
                } else {
                    throw new OCSPException("OCSP: basic response must provide name or key hash");
                }
            }

            if (ocspResponderCertificate == null) {
                throw new OCSPException("OCSP: certificate for responder " + name + " not found");
            }

            try {
                SigUtils.checkResponderCertificateUsage(ocspResponderCertificate);
            } catch (CertificateParsingException ex) {
                LOG.error(ex, ex);
            }

            checkOcspSignature(ocspResponderCertificate, basicResponse);
            boolean nonceChecked = checkNonce(basicResponse);
            SingleResp[] responses = basicResponse.getResponses();

            if (responses.length != 1) {
                throw new OCSPException(
                        "OCSP: Received " + responses.length + " responses instead of 1!");
            }

            SingleResp resp = responses[0];
            Object status = resp.getCertStatus();

            if (!nonceChecked) {
                checkOcspResponseFresh(resp);
            }

            if (status instanceof RevokedStatus) {
                RevokedStatus revokedStatus = (RevokedStatus) status;
                if (revokedStatus.getRevocationTime().compareTo(signDate) <= 0) {
                    throw new RevokedCertificateException(
                            "OCSP: Certificate is revoked since " +
                                    revokedStatus.getRevocationTime(),
                            revokedStatus.getRevocationTime());
                }
                LOG.info("The certificate was revoked after signing by OCSP " + ocspUrl +
                        " on " + revokedStatus.getRevocationTime());
            } else if (status != CertificateStatus.GOOD) {
                throw new OCSPException("OCSP: Status of Cert is unknown");
            }
        }
    }

    private byte[] getKeyHashFromCertHolder(X509CertificateHolder certHolder) {
        SubjectPublicKeyInfo info = certHolder.getSubjectPublicKeyInfo();
        try {
            return MessageDigest.getInstance("SHA-1").digest(info.getPublicKeyData().getBytes());
        } catch (NoSuchAlgorithmException ex) {
            LOG.error("SHA-1 Algorithm not found", ex);
            return new byte[0];
        }
    }

    private void findResponderCertificateByKeyHash(BasicOCSPResp basicResponse, byte[] keyHash)
            throws IOException {
        X509CertificateHolder[] certHolders = basicResponse.getCerts();
        for (X509CertificateHolder certHolder : certHolders) {
            byte[] digest = getKeyHashFromCertHolder(certHolder);
            if (Arrays.equals(keyHash, digest)) {
                try {
                    ocspResponderCertificate = certificateConverter.getCertificate(certHolder);
                    return;
                } catch (CertificateException ex) {
                    // unlikely to happen because the certificate existed as an object
                    LOG.error(ex, ex);
                }
                break;
            }
        }

        for (X509Certificate cert : additionalCerts) {
            try {
                byte[] digest = getKeyHashFromCertHolder(new X509CertificateHolder(cert.getEncoded()));
                if (Arrays.equals(keyHash, digest)) {
                    ocspResponderCertificate = cert;
                    return;
                }
            } catch (CertificateEncodingException ex) {
                LOG.error(ex, ex);
            }
        }
    }

    private void findResponderCertificateByName(BasicOCSPResp basicResponse, X500Name name) {
        X509CertificateHolder[] certHolders = basicResponse.getCerts();
        for (X509CertificateHolder certHolder : certHolders) {
            if (name.equals(certHolder.getSubject())) {
                try {
                    ocspResponderCertificate = certificateConverter.getCertificate(certHolder);
                    return;
                } catch (CertificateException ex) {
                    // unlikely to happen because the certificate existed as an object
                    LOG.error(ex, ex);
                }
            }
        }

        for (X509Certificate cert : additionalCerts) {
            X500Name certSubjectName = new X500Name(cert.getSubjectX500Principal().getName());
            if (certSubjectName.equals(name)) {
                ocspResponderCertificate = cert;
                return;
            }
        }
    }

    private void checkOcspResponseFresh(SingleResp resp) throws OCSPException {
        Date curDate = Calendar.getInstance().getTime();
        Date thisUpdate = resp.getThisUpdate();

        if (thisUpdate == null) {
            throw new OCSPException("OCSP: thisUpdate field is missing in response (RFC 5019 2.2.4.)");
        }

        Date nextUpdate = resp.getNextUpdate();
        if (nextUpdate == null) {
            throw new OCSPException("OCSP: nextUpdate field is missing in response (RFC 5019 2.2.4.)");
        }

        if (curDate.compareTo(thisUpdate) < 0) {
            LOG.error(curDate + " < " + thisUpdate);
            throw new OCSPException("OCSP: current date < thisUpdate field (RFC 5019 2.2.4.)");
        }

        if (curDate.compareTo(nextUpdate) > 0) {
            LOG.error(curDate + " > " + nextUpdate);
            throw new OCSPException("OCSP: current date > nextUpdate field (RFC 5019 2.2.4.)");
        }

        LOG.info("OCSP response is fresh");
    }

    private void checkOcspSignature(X509Certificate certificate, BasicOCSPResp basicResponse)
            throws OCSPException, IOException {
        try {
            ContentVerifierProvider verifier = new JcaContentVerifierProviderBuilder()
                    .setProvider(SecurityProvider.getProvider()).build(certificate);

            if (!basicResponse.isSignatureValid(verifier)) {
                throw new OCSPException("OCSP-Signature is not valid!");
            }
        } catch (OperatorCreationException e) {
            throw new OCSPException("Error checking Ocsp-Signature", e);
        }
    }

    private boolean checkNonce(BasicOCSPResp basicResponse) throws OCSPException {
        Extension nonceExt = basicResponse.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        if (nonceExt != null) {
            DEROctetString responseNonceString = (DEROctetString) nonceExt.getExtnValue();
            if (!responseNonceString.equals(encodedNonce)) {
                throw new OCSPException("Different nonce found in response!");
            } else {
                LOG.info("Nonce is good");
                return true;
            }
        }

        return false;
    }

    private OCSPResp performRequest(String urlString)
            throws IOException, OCSPException, URISyntaxException {
        OCSPReq request = generateOCSPRequest();
        URL url = new URI(urlString).toURL();
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        try {
            httpConnection.setRequestProperty("Content-Type", "application/ocsp-request");
            httpConnection.setRequestProperty("Accept", "application/ocsp-response");
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);

            try (OutputStream out = httpConnection.getOutputStream()) {
                out.write(request.getEncoded());
            }

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                String location = httpConnection.getHeaderField("Location");

                if (urlString.startsWith("http://") &&
                        location.startsWith("https://") &&
                        urlString.substring(7).equals(location.substring(8))) {
                    LOG.info("redirection to " + location + " followed");
                    return performRequest(location);
                } else {
                    LOG.info("redirection to " + location + " ignored");
                }
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("OCSP: Could not access url, ResponseCode "
                        + httpConnection.getResponseCode() + ": "
                        + httpConnection.getResponseMessage());
            }

            try (InputStream in = (InputStream) httpConnection.getContent()) {
                return new OCSPResp(in);
            }
        } finally {
            httpConnection.disconnect();
        }
    }

    public void verifyRespStatus(OCSPResp resp) throws OCSPException {
        String statusInfo = "";
        if (resp != null) {
            int status = resp.getStatus();
            switch (status) {
                case OCSPResponseStatus.INTERNAL_ERROR:
                    statusInfo = "INTERNAL_ERROR";
                    LOG.error("An internal error occurred in the OCSP Server!");
                    break;
                case OCSPResponseStatus.MALFORMED_REQUEST:
                    statusInfo = "MALFORMED_REQUEST";
                    LOG.error("Your request did not fit the RFC 2560 syntax!");
                    break;
                case OCSPResponseStatus.SIG_REQUIRED:
                    statusInfo = "SIG_REQUIRED";
                    LOG.error("Your request was not signed!");
                    break;
                case OCSPResponseStatus.TRY_LATER:
                    statusInfo = "TRY_LATER";
                    LOG.error("The server was too busy to answer you!");
                    break;
                case OCSPResponseStatus.UNAUTHORIZED:
                    statusInfo = "UNAUTHORIZED";
                    LOG.error("The server could not authenticate you!");
                    break;
                case OCSPResponseStatus.SUCCESSFUL:
                    break;
                default:
                    statusInfo = "UNKNOWN";
                    LOG.error("Unknown OCSPResponse status code! " + status);
            }
        }

        if (resp == null || resp.getStatus() != OCSPResponseStatus.SUCCESSFUL) {
            throw new OCSPException("OCSP response unsuccessful, status: " + statusInfo);
        }
    }

    private OCSPReq generateOCSPRequest() throws OCSPException, IOException {
        Security.addProvider(SecurityProvider.getProvider());
        CertificateID certId;

        try {
            certId = new CertificateID(new SHA1DigestCalculator(),
                    new JcaX509CertificateHolder(issuerCertificate),
                    certificateToCheck.getSerialNumber());
        } catch (CertificateEncodingException e) {
            throw new IOException("Error creating CertificateID with the Certificate encoding", e);
        }

        Extension responseExtension = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_response,
                false, new DLSequence(OCSPObjectIdentifiers.id_pkix_ocsp_basic).getEncoded());

        encodedNonce = new DEROctetString(new DEROctetString(create16BytesNonce()));
        Extension nonceExtension = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false,
                encodedNonce);

        OCSPReqBuilder builder = new OCSPReqBuilder();
        builder.setRequestExtensions(
                new Extensions(new Extension[]{responseExtension, nonceExtension}));
        builder.addRequest(certId);
        return builder.build();
    }

    private byte[] create16BytesNonce() {
        byte[] nonce = new byte[16];
        RANDOM.nextBytes(nonce);
        return nonce;
    }

    private static class SHA1DigestCalculator implements DigestCalculator {
        private final ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }

        @Override
        public OutputStream getOutputStream() {
            return bOut;
        }

        @Override
        public byte[] getDigest() {
            byte[] bytes = bOut.toByteArray();
            bOut.reset();

            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                return md.digest(bytes);
            } catch (NoSuchAlgorithmException ex) {
                LOG.error("SHA-1 Algorithm not found", ex);
                return new byte[0];
            }
        }
    }
}
