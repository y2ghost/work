package study.ywork.doc.pdfbox.signature.cert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import study.ywork.doc.pdfbox.signature.SigUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public final class CertificateVerifier {
    private static final Log LOG = LogFactory.getLog(CertificateVerifier.class);

    private CertificateVerifier() {

    }

    public static PKIXCertPathBuilderResult verifyCertificate(
            X509Certificate cert, Set<X509Certificate> additionalCerts,
            boolean verifySelfSignedCert, Date signDate)
            throws CertificateVerificationException {
        try {
            // 检查自签名证书
            if (!verifySelfSignedCert && isSelfSigned(cert)) {
                throw new CertificateVerificationException("The certificate is self-signed.");
            }

            Set<X509Certificate> certSet = new HashSet<>(additionalCerts);
            Set<X509Certificate> certsToTrySet = new HashSet<>();
            certsToTrySet.add(cert);
            certsToTrySet.addAll(additionalCerts);
            int downloadSize = 0;

            while (!certsToTrySet.isEmpty()) {
                Set<X509Certificate> nextCertsToTrySet = new HashSet<>();
                for (X509Certificate tryCert : certsToTrySet) {
                    Set<X509Certificate> downloadedExtraCertificatesSet =
                            CertificateVerifier.downloadExtraCertificates(tryCert);
                    for (X509Certificate downloadedCertificate : downloadedExtraCertificatesSet) {
                        if (!certSet.contains(downloadedCertificate)) {
                            nextCertsToTrySet.add(downloadedCertificate);
                            certSet.add(downloadedCertificate);
                            downloadSize++;
                        }
                    }
                }

                certsToTrySet = nextCertsToTrySet;
            }

            if (downloadSize > 0) {
                LOG.info("CA issuers: " + downloadSize + " downloaded certificate(s) are new");
            }

            Set<X509Certificate> intermediateCerts = new HashSet<>();
            Set<TrustAnchor> trustAnchors = new HashSet<>();

            for (X509Certificate additionalCert : certSet) {
                if (isSelfSigned(additionalCert)) {
                    trustAnchors.add(new TrustAnchor(additionalCert, null));
                } else {
                    intermediateCerts.add(additionalCert);
                }
            }

            if (trustAnchors.isEmpty()) {
                throw new CertificateVerificationException("No root certificate in the chain");
            }

            PKIXCertPathBuilderResult verifiedCertChain = verifyCertificate(
                    cert, trustAnchors, intermediateCerts, signDate);
            LOG.info("Certification chain verified successfully up to this root: " +
                    verifiedCertChain.getTrustAnchor().getTrustedCert().getSubjectX500Principal());
            checkRevocations(cert, certSet, signDate);
            return verifiedCertChain;
        } catch (CertPathBuilderException certPathEx) {
            throw new CertificateVerificationException(
                    "Error building certification path: "
                            + cert.getSubjectX500Principal(), certPathEx);
        } catch (CertificateVerificationException cvex) {
            LOG.error(cvex.getMessage());
            throw cvex;
        } catch (IOException | URISyntaxException |
                 GeneralSecurityException | RevokedCertificateException | OCSPException ex) {
            throw new CertificateVerificationException(
                    "Error verifying the certificate: "
                            + cert.getSubjectX500Principal(), ex);
        }
    }

    private static void checkRevocations(X509Certificate cert,
                                         Set<X509Certificate> additionalCerts,
                                         Date signDate)
            throws IOException, CertificateVerificationException, OCSPException,
            RevokedCertificateException, GeneralSecurityException, URISyntaxException {
        if (isSelfSigned(cert)) {
            // NOOP
            return;
        }

        for (X509Certificate additionalCert : additionalCerts) {
            try {
                cert.verify(additionalCert.getPublicKey(), SecurityProvider.getProvider());
                checkRevocationsWithIssuer(cert, additionalCert, additionalCerts, signDate);
            } catch (GeneralSecurityException ex) {
                // 没有签发者
            }
        }
    }

    private static void checkRevocationsWithIssuer(X509Certificate cert, X509Certificate issuerCert,
                                                   Set<X509Certificate> additionalCerts, Date signDate)
            throws OCSPException, CertificateVerificationException, RevokedCertificateException,
            GeneralSecurityException, IOException, URISyntaxException {
        String ocspURL = extractOCSPURL(cert);
        if (ocspURL != null) {
            OcspHelper ocspHelper = new OcspHelper(cert, signDate, issuerCert, additionalCerts, ocspURL);
            try {
                verifyOCSP(ocspHelper, additionalCerts);
            } catch (IOException | OCSPException ex) {
                LOG.warn("Exception trying OCSP, will try CRL", ex);
                LOG.warn("Certificate# to check: " + cert.getSerialNumber().toString(16));
                CRLVerifier.verifyCertificateCRLs(cert, signDate, additionalCerts);
            }
        } else {
            LOG.info("OCSP not available, will try CRL");
            CRLVerifier.verifyCertificateCRLs(cert, signDate, additionalCerts);
        }

        checkRevocations(issuerCert, additionalCerts, signDate);
    }

    public static boolean isSelfSigned(X509Certificate cert) {
        try {
            PublicKey key = cert.getPublicKey();
            cert.verify(key, SecurityProvider.getProvider());
            return true;
        } catch (GeneralSecurityException | IllegalArgumentException | IOException ex) {
            LOG.debug("Couldn't get signature information - returning false", ex);
            return false;
        }
    }

    public static Set<X509Certificate> downloadExtraCertificates(X509Extension ext) {
        Set<X509Certificate> resultSet = new HashSet<>();
        byte[] authorityExtensionValue = ext.getExtensionValue(Extension.authorityInfoAccess.getId());

        if (authorityExtensionValue == null) {
            return resultSet;
        }

        ASN1Primitive asn1Prim;
        try {
            asn1Prim = JcaX509ExtensionUtils.parseExtensionValue(authorityExtensionValue);
        } catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
            return resultSet;
        }

        if (!(asn1Prim instanceof ASN1Sequence)) {
            LOG.warn("ASN1Sequence expected, got " + asn1Prim.getClass().getSimpleName());
            return resultSet;
        }

        ASN1Sequence asn1Seq = (ASN1Sequence) asn1Prim;
        Enumeration<?> objects = asn1Seq.getObjects();

        while (objects.hasMoreElements()) {
            ASN1Sequence obj = (ASN1Sequence) objects.nextElement();
            ASN1Encodable oid = obj.getObjectAt(0);

            if (!X509ObjectIdentifiers.id_ad_caIssuers.equals(oid)) {
                continue;
            }

            ASN1TaggedObject location = (ASN1TaggedObject) obj.getObjectAt(1);
            ASN1OctetString uri = (ASN1OctetString) location.getBaseObject();
            String urlString = new String(uri.getOctets(), StandardCharsets.UTF_8);
            LOG.info("CA issuers URL: " + urlString);

            try (InputStream in = SigUtils.openURL(urlString)) {
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                Collection<? extends Certificate> altCerts = certFactory.generateCertificates(in);
                altCerts.forEach(altCert -> resultSet.add((X509Certificate) altCert));
                LOG.info("CA issuers URL: " + altCerts.size() + " certificate(s) downloaded");
            } catch (IOException | URISyntaxException ex) {
                LOG.warn(urlString + " failure: " + ex.getMessage(), ex);
            } catch (CertificateException ex) {
                LOG.warn(ex.getMessage(), ex);
            }
        }

        LOG.info("CA issuers: Downloaded " + resultSet.size() + " certificate(s) total");
        return resultSet;
    }

    private static PKIXCertPathBuilderResult verifyCertificate(
            X509Certificate cert, Set<TrustAnchor> trustAnchors,
            Set<X509Certificate> intermediateCerts, Date signDate)
            throws GeneralSecurityException {
        X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(cert);
        PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(trustAnchors, selector);
        pkixParams.setRevocationEnabled(false);
        pkixParams.setPolicyQualifiersRejected(false);
        pkixParams.setDate(signDate);
        CertStore intermediateCertStore = CertStore.getInstance("Collection",
                new CollectionCertStoreParameters(intermediateCerts));
        pkixParams.addCertStore(intermediateCertStore);
        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
        return (PKIXCertPathBuilderResult) builder.build(pkixParams);
    }

    private static String extractOCSPURL(X509Certificate cert) throws IOException {
        byte[] authorityExtensionValue = cert.getExtensionValue(Extension.authorityInfoAccess.getId());
        if (authorityExtensionValue != null) {
            ASN1Sequence asn1Seq = (ASN1Sequence) JcaX509ExtensionUtils.parseExtensionValue(authorityExtensionValue);
            Enumeration<?> objects = asn1Seq.getObjects();
            while (objects.hasMoreElements()) {
                ASN1Sequence obj = (ASN1Sequence) objects.nextElement();
                ASN1Encodable oid = obj.getObjectAt(0);
                ASN1TaggedObject location = (ASN1TaggedObject) obj.getObjectAt(1);

                if (X509ObjectIdentifiers.id_ad_ocsp.equals(oid)
                        && location.getTagNo() == GeneralName.uniformResourceIdentifier) {
                    ASN1OctetString url = (ASN1OctetString) location.getBaseObject();
                    String ocspURL = new String(url.getOctets(), StandardCharsets.UTF_8);
                    LOG.info("OCSP URL: " + ocspURL);
                    return ocspURL;
                }
            }
        }

        return null;
    }

    private static void verifyOCSP(OcspHelper ocspHelper, Set<X509Certificate> additionalCerts)
            throws RevokedCertificateException, IOException, OCSPException,
            CertificateVerificationException, URISyntaxException {
        Date now = Calendar.getInstance().getTime();
        OCSPResp ocspResponse;
        ocspResponse = ocspHelper.getResponseOcsp();

        if (ocspResponse.getStatus() != OCSPResp.SUCCESSFUL) {
            throw new CertificateVerificationException("OCSP check not successful, status: "
                    + ocspResponse.getStatus());
        }

        LOG.info("OCSP check successful");
        BasicOCSPResp basicResponse = (BasicOCSPResp) ocspResponse.getResponseObject();
        X509Certificate ocspResponderCertificate = ocspHelper.getOcspResponderCertificate();

        if (ocspResponderCertificate.getExtensionValue(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck.getId()) != null) {
            LOG.info("Revocation check of OCSP responder certificate skipped (id-pkix-ocsp-nocheck is set)");
            return;
        }

        if (ocspHelper.getCertificateToCheck().equals(ocspResponderCertificate)) {
            LOG.info("OCSP responder certificate is identical to certificate to check");
            return;
        }

        LOG.info("Check of OCSP responder certificate");
        Set<X509Certificate> additionalCerts2 = new HashSet<>(additionalCerts);
        JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();

        for (X509CertificateHolder certHolder : basicResponse.getCerts()) {
            try {
                X509Certificate cert = certificateConverter.getCertificate(certHolder);
                if (!ocspResponderCertificate.equals(cert)) {
                    additionalCerts2.add(cert);
                }
            } catch (CertificateException ex) {
                LOG.error(ex, ex);
            }
        }

        CertificateVerifier.verifyCertificate(ocspResponderCertificate, additionalCerts2, true, now);
        LOG.info("Check of OCSP responder certificate done");
    }
}
