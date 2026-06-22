package study.ywork.doc.pdfbox.signature.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSUpdateInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.util.Hex;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import study.ywork.doc.pdfbox.signature.SigUtils;
import study.ywork.doc.pdfbox.signature.cert.CRLVerifier;
import study.ywork.doc.pdfbox.signature.cert.CertificateVerificationException;
import study.ywork.doc.pdfbox.signature.cert.OcspHelper;
import study.ywork.doc.pdfbox.signature.cert.RevokedCertificateException;
import study.ywork.doc.pdfbox.signature.validation.CertInformationCollector.CertSignatureInformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddValidationInformation {
    private static final Log LOG = LogFactory.getLog(AddValidationInformation.class);
    private CertInformationCollector certInformationHelper;
    private COSArray correspondingOCSPs;
    private COSArray correspondingCRLs;
    private COSDictionary vriBase;
    private COSArray ocsps;
    private COSArray crls;
    private COSArray certs;
    private final Map<X509Certificate, COSStream> certMap = new HashMap<>();
    private PDDocument document;
    private final Set<X509Certificate> foundRevocationInformation = new HashSet<>();
    private Calendar signDate;
    // foundRevocationInformation 和 ocspChecked作用差不多，因为freetsa的问题
    // 需要注意ocspChecked最后添加哈
    private final Set<X509Certificate> ocspChecked = new HashSet<>();

    public void validateSignature(File inFile, File outFile) throws IOException {
        if (inFile == null || !inFile.exists()) {
            String err = "Document for signing ";
            if (null == inFile) {
                err += "is null";
            } else {
                err += "does not exist: " + inFile.getAbsolutePath();
            }
            throw new FileNotFoundException(err);
        }

        try (PDDocument doc = Loader.loadPDF(inFile);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            int accessPermissions = SigUtils.getMDPPermission(doc);
            if (accessPermissions == 1) {
                System.out.println("PDF is certified to forbid changes, "
                        + "some readers may report the document as invalid despite that "
                        + "the PDF specification allows DSS additions");
            }

            document = doc;
            doValidation(inFile.getAbsolutePath(), fos);
        }
    }

    private void doValidation(String filename, OutputStream output) throws IOException {
        certInformationHelper = new CertInformationCollector();
        CertSignatureInformation certInfo = null;

        try {
            PDSignature signature = SigUtils.getLastRelevantSignature(document);
            if (signature != null) {
                certInfo = certInformationHelper.getLastCertInfo(signature, filename);
                signDate = signature.getSignDate();
                if ("ETSI.RFC3161".equals(signature.getSubFilter())) {
                    byte[] contents = signature.getContents();
                    TimeStampToken timeStampToken = new TimeStampToken(new CMSSignedData(contents));
                    TimeStampTokenInfo timeStampInfo = timeStampToken.getTimeStampInfo();
                    signDate = Calendar.getInstance();
                    signDate.setTime(timeStampInfo.getGenTime());
                }
            }
        } catch (TSPException | CMSException | CertificateProccessingException e) {
            throw new IOException("An Error occurred processing the Signature", e);
        }

        if (certInfo == null) {
            throw new IOException(
                    "No Certificate information or signature found in the given document");
        }

        PDDocumentCatalog docCatalog = document.getDocumentCatalog();
        COSDictionary catalog = docCatalog.getCOSObject();
        catalog.setNeedToBeUpdated(true);
        COSDictionary dss = getOrCreateDictionaryEntry(COSDictionary.class, catalog, "DSS");
        addExtensions(docCatalog);
        vriBase = getOrCreateDictionaryEntry(COSDictionary.class, dss, "VRI");
        ocsps = getOrCreateDictionaryEntry(COSArray.class, dss, "OCSPs");
        crls = getOrCreateDictionaryEntry(COSArray.class, dss, "CRLs");
        certs = getOrCreateDictionaryEntry(COSArray.class, dss, "Certs");
        addRevocationData(certInfo);
        addAllCertsToCertArray();
        document.saveIncremental(output);
    }

    private static <T extends COSBase & COSUpdateInfo> T getOrCreateDictionaryEntry(Class<T> clazz,
                                                                                    COSDictionary parent, String name) throws IOException {
        T result;
        COSBase element = parent.getDictionaryObject(name);

        if (element != null && clazz.isInstance(element)) {
            result = clazz.cast(element);
            result.setNeedToBeUpdated(true);
        } else if (element != null) {
            throw new IOException("Element " + name + " from dictionary is not of type "
                    + clazz.getCanonicalName());
        } else {
            try {
                result = clazz.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException | SecurityException e) {
                throw new IOException("Failed to create new instance of " + clazz.getCanonicalName(), e);
            }
            result.setDirect(false);
            parent.setItem(COSName.getPDFName(name), result);
        }

        return result;
    }

    private void addRevocationData(CertSignatureInformation certInfo) throws IOException {
        COSDictionary vri = new COSDictionary();
        vriBase.setItem(certInfo.getSignatureHash(), vri);
        updateVRI(certInfo, vri);

        if (certInfo.getTsaCerts() != null) {
            correspondingOCSPs = null;
            correspondingCRLs = null;
            addRevocationDataRecursive(certInfo.getTsaCerts());
        }
    }

    private void addRevocationDataRecursive(CertSignatureInformation certInfo) throws IOException {
        if (certInfo.isSelfSigned()) {
            return;
        }

        boolean isRevocationInfoFound = foundRevocationInformation.contains(certInfo.getCertificate());
        if (!isRevocationInfoFound) {
            if (certInfo.getOcspUrl() != null && !certInfo.getIssuerCertificates().isEmpty()) {
                isRevocationInfoFound = fetchOcspData(certInfo);
            }

            if (!isRevocationInfoFound && certInfo.getCrlUrl() != null) {
                fetchCrlData(certInfo);
                isRevocationInfoFound = true;
            }

            if (certInfo.getOcspUrl() == null && certInfo.getCrlUrl() == null) {
                LOG.info("No revocation information for cert " + certInfo.getCertificate().getSubjectX500Principal());
            } else if (!isRevocationInfoFound) {
                throw new IOException("Could not fetch Revocation Info for Cert: "
                        + certInfo.getCertificate().getSubjectX500Principal());
            }
        }

        if (certInfo.getAlternativeCertChain() != null) {
            addRevocationDataRecursive(certInfo.getAlternativeCertChain());
        }

        if (certInfo.getCertChain() != null && certInfo.getCertChain().getCertificate() != null) {
            addRevocationDataRecursive(certInfo.getCertChain());
        }
    }

    private boolean fetchOcspData(CertSignatureInformation certInfo) throws IOException {
        try {
            addOcspData(certInfo);
            return true;
        } catch (OCSPException | CertificateProccessingException | IOException | URISyntaxException e) {
            LOG.error("Failed fetching OCSP at " + certInfo.getOcspUrl(), e);
            return false;
        } catch (RevokedCertificateException e) {
            throw new IOException(e);
        }
    }

    private void fetchCrlData(CertSignatureInformation certInfo) throws IOException {
        try {
            addCrlRevocationInfo(certInfo);
        } catch (GeneralSecurityException | IOException | URISyntaxException |
                 RevokedCertificateException | CertificateVerificationException e) {
            LOG.warn("Failed fetching CRL", e);
            throw new IOException(e);
        }
    }

    private void addOcspData(CertSignatureInformation certInfo) throws IOException, OCSPException,
            CertificateProccessingException, RevokedCertificateException, URISyntaxException {
        X509Certificate certificate = certInfo.getCertificate();
        if (ocspChecked.contains(certificate)) {
            return;
        }

        for (X509Certificate issuerCertificate : certInfo.getIssuerCertificates()) {
            addOcspData(certificate, issuerCertificate, certInfo.getOcspUrl());
        }
    }

    private void addOcspData(X509Certificate certificate, X509Certificate issuerCertificate, String ocspURL)
            throws IOException, OCSPException, CertificateProccessingException,
            RevokedCertificateException, URISyntaxException {
        OcspHelper ocspHelper = new OcspHelper(
                certificate,
                signDate.getTime(),
                issuerCertificate,
                new HashSet<>(certInformationHelper.getCertificateSet()),
                ocspURL);
        OCSPResp ocspResp = ocspHelper.getResponseOcsp();
        ocspChecked.add(certificate);
        BasicOCSPResp basicResponse = (BasicOCSPResp) ocspResp.getResponseObject();
        X509Certificate ocspResponderCertificate = ocspHelper.getOcspResponderCertificate();
        certInformationHelper.addAllCertsFromHolders(basicResponse.getCerts());
        byte[] signatureHash;

        try {
            BEROctetString encodedSignature = new BEROctetString(basicResponse.getSignature());
            signatureHash = MessageDigest.getInstance("SHA-1").digest(encodedSignature.getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            throw new CertificateProccessingException(ex);
        }

        String signatureHashHex = Hex.getString(signatureHash);
        if (!vriBase.containsKey(signatureHashHex)) {
            COSArray savedCorrespondingOCSPs = correspondingOCSPs;
            COSArray savedCorrespondingCRLs = correspondingCRLs;

            COSDictionary vri = new COSDictionary();
            vriBase.setItem(signatureHashHex, vri);
            CertSignatureInformation ocspCertInfo = certInformationHelper.getCertInfo(ocspResponderCertificate);

            updateVRI(ocspCertInfo, vri);

            correspondingOCSPs = savedCorrespondingOCSPs;
            correspondingCRLs = savedCorrespondingCRLs;
        }

        byte[] ocspData = ocspResp.getEncoded();
        COSStream ocspStream = writeDataToStream(ocspData);
        ocsps.add(ocspStream);

        if (correspondingOCSPs != null) {
            correspondingOCSPs.add(ocspStream);
        }
        foundRevocationInformation.add(certificate);
    }

    private void addCrlRevocationInfo(CertSignatureInformation certInfo)
            throws IOException, RevokedCertificateException, GeneralSecurityException,
            CertificateVerificationException, URISyntaxException {
        X509CRL crl = CRLVerifier.downloadCRLFromWeb(certInfo.getCrlUrl());
        X509Certificate issuerCertificate = null;

        for (X509Certificate certificate : certInformationHelper.getCertificateSet()) {
            if (certificate.getSubjectX500Principal().equals(crl.getIssuerX500Principal())) {
                issuerCertificate = certificate;
                break;
            }
        }

        if (issuerCertificate == null) {
            throw new CertificateVerificationException("Can't find issuer of CRL for " + certInfo.getCrlUrl());
        }

        crl.verify(issuerCertificate.getPublicKey(), SecurityProvider.getProvider().getName());
        CRLVerifier.checkRevocation(crl, certInfo.getCertificate(), signDate.getTime(), certInfo.getCrlUrl());
        COSStream crlStream = writeDataToStream(crl.getEncoded());
        crls.add(crlStream);

        if (correspondingCRLs != null) {
            correspondingCRLs.add(crlStream);
            byte[] signatureHash;

            try {
                BEROctetString berEncodedSignature = new BEROctetString(crl.getSignature());
                signatureHash = MessageDigest.getInstance("SHA-1").digest(berEncodedSignature.getEncoded());
            } catch (NoSuchAlgorithmException ex) {
                throw new CertificateVerificationException(ex.getMessage(), ex);
            }

            String signatureHashHex = Hex.getString(signatureHash);
            if (!vriBase.containsKey(signatureHashHex)) {
                COSArray savedCorrespondingOCSPs = correspondingOCSPs;
                COSArray savedCorrespondingCRLs = correspondingCRLs;

                COSDictionary vri = new COSDictionary();
                vriBase.setItem(signatureHashHex, vri);

                CertSignatureInformation crlCertInfo;
                try {
                    crlCertInfo = certInformationHelper.getCertInfo(issuerCertificate);
                } catch (CertificateProccessingException ex) {
                    throw new CertificateVerificationException(ex.getMessage(), ex);
                }

                updateVRI(crlCertInfo, vri);
                correspondingOCSPs = savedCorrespondingOCSPs;
                correspondingCRLs = savedCorrespondingCRLs;
            }
        }

        foundRevocationInformation.add(certInfo.getCertificate());
    }

    private void updateVRI(CertSignatureInformation certInfo, COSDictionary vri) throws IOException {
        if (certInfo.getCertificate().getExtensionValue(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck.getId()) == null) {
            correspondingOCSPs = new COSArray();
            correspondingCRLs = new COSArray();
            addRevocationDataRecursive(certInfo);

            if (correspondingOCSPs.size() > 0) {
                vri.setItem(COSName.OCSP, correspondingOCSPs);
            }

            if (correspondingCRLs.size() > 0) {
                vri.setItem(COSName.CRL, correspondingCRLs);
            }
        }

        COSArray correspondingCerts = new COSArray();
        CertSignatureInformation ci = certInfo;

        do {
            X509Certificate cert = ci.getCertificate();
            try {
                COSStream certStream = writeDataToStream(cert.getEncoded());
                correspondingCerts.add(certStream);
                certMap.put(cert, certStream);
            } catch (CertificateEncodingException ex) {
                LOG.error(ex, ex);
            }

            if (cert.getExtensionValue(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck.getId()) != null) {
                break;
            }

            ci = ci.getCertChain();
        } while (ci != null);

        vri.setItem(COSName.CERT, correspondingCerts);
        vri.setDate(COSName.TU, Calendar.getInstance());
    }

    private void addAllCertsToCertArray() throws IOException {
        for (X509Certificate cert : certInformationHelper.getCertificateSet()) {
            if (!certMap.containsKey(cert)) {
                try {
                    COSStream certStream = writeDataToStream(cert.getEncoded());
                    certMap.put(cert, certStream);
                } catch (CertificateEncodingException ex) {
                    throw new IOException(ex);
                }
            }
        }
        certMap.values().forEach(certStream -> certs.add(certStream));
    }

    private COSStream writeDataToStream(byte[] data) throws IOException {
        COSStream stream = document.getDocument().createCOSStream();
        try (OutputStream os = stream.createOutputStream(COSName.FLATE_DECODE)) {
            os.write(data);
        }

        return stream;
    }

    private void addExtensions(PDDocumentCatalog catalog) {
        COSDictionary dssExtensions = new COSDictionary();
        dssExtensions.setDirect(true);
        catalog.getCOSObject().setItem(COSName.EXTENSIONS, dssExtensions);

        COSDictionary adbeExtension = new COSDictionary();
        adbeExtension.setDirect(true);
        dssExtensions.setItem(COSName.ADBE, adbeExtension);

        adbeExtension.setName(COSName.BASE_VERSION, "1.7");
        adbeExtension.setInt(COSName.EXTENSION_LEVEL, 5);

        catalog.setVersion("1.7");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
            System.exit(1);
        }

        Security.addProvider(SecurityProvider.getProvider());
        AddValidationInformation addOcspInformation = new AddValidationInformation();
        File inFile = new File(args[0]);
        String name = inFile.getName();
        String substring = name.substring(0, name.lastIndexOf('.'));
        File outFile = new File(inFile.getParent(), substring + "_LTV.pdf");
        addOcspInformation.validateSignature(inFile, outFile);
    }

    private static void usage() {
        System.err.println("usage: java " + AddValidationInformation.class.getName() + " "
                + "<pdf_to_add_ocsp>\n");
    }
}
