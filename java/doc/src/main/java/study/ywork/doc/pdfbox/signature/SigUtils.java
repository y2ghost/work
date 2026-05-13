package study.ywork.doc.pdfbox.signature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Store;
import study.ywork.doc.pdfbox.signature.cert.CertificateVerificationException;
import study.ywork.doc.pdfbox.signature.cert.CertificateVerifier;
import study.ywork.doc.pdfbox.util.ConnectedInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class SigUtils {
    private static final Log LOG = LogFactory.getLog(SigUtils.class);

    private SigUtils() {
    }

    public static int getMDPPermission(PDDocument doc) {
        COSDictionary permsDict = doc.getDocumentCatalog().getCOSObject()
                .getCOSDictionary(COSName.PERMS);
        if (permsDict != null) {
            COSDictionary signatureDict = permsDict.getCOSDictionary(COSName.DOCMDP);
            if (signatureDict != null) {
                COSArray refArray = signatureDict.getCOSArray(COSName.REFERENCE);
                if (refArray != null) {
                    for (int i = 0; i < refArray.size(); ++i) {
                        COSBase base = refArray.getObject(i);
                        if (base instanceof COSDictionary sigRefDict && COSName.DOCMDP.equals(sigRefDict.getDictionaryObject(COSName.TRANSFORM_METHOD))) {
                            base = sigRefDict.getDictionaryObject(COSName.TRANSFORM_PARAMS);
                            if (base instanceof COSDictionary tempDict) {
                                int accessPermissions = tempDict.getInt(COSName.P, 2);
                                if (accessPermissions < 1 || accessPermissions > 3) {
                                    accessPermissions = 2;
                                }
                                return accessPermissions;
                            }
                        }

                    }
                }
            }
        }
        return 0;
    }

    public static void setMDPPermission(PDDocument doc, PDSignature signature, int accessPermissions)
            throws IOException {
        for (PDSignature sig : doc.getSignatureDictionaries()) {
            if (COSName.DOC_TIME_STAMP.equals(sig.getCOSObject().getItem(COSName.TYPE))) {
                continue;
            }

            if (sig.getCOSObject().containsKey(COSName.CONTENTS)) {
                throw new IOException("DocMDP transform method not allowed if an approval signature exists");
            }
        }

        COSDictionary sigDict = signature.getCOSObject();
        COSDictionary transformParameters = new COSDictionary();
        transformParameters.setItem(COSName.TYPE, COSName.TRANSFORM_PARAMS);
        transformParameters.setInt(COSName.P, accessPermissions);
        transformParameters.setName(COSName.V, "1.2");
        transformParameters.setNeedToBeUpdated(true);
        transformParameters.setDirect(true);

        COSDictionary referenceDict = new COSDictionary();
        referenceDict.setItem(COSName.TYPE, COSName.SIG_REF);
        referenceDict.setItem(COSName.TRANSFORM_METHOD, COSName.DOCMDP);
        referenceDict.setItem(COSName.DIGEST_METHOD, COSName.getPDFName("SHA1"));
        referenceDict.setItem(COSName.TRANSFORM_PARAMS, transformParameters);
        referenceDict.setNeedToBeUpdated(true);
        referenceDict.setDirect(true);

        COSArray referenceArray = new COSArray();
        referenceArray.add(referenceDict);
        sigDict.setItem(COSName.REFERENCE, referenceArray);
        referenceArray.setNeedToBeUpdated(true);
        referenceArray.setDirect(true);

        COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
        COSDictionary permsDict = catalogDict.getCOSDictionary(COSName.PERMS);
        if (permsDict == null) {
            permsDict = new COSDictionary();
            catalogDict.setItem(COSName.PERMS, permsDict);
        }
        permsDict.setItem(COSName.DOCMDP, signature);
        catalogDict.setNeedToBeUpdated(true);
        permsDict.setNeedToBeUpdated(true);
    }

    public static void checkCertificateUsage(X509Certificate x509Certificate)
            throws CertificateParsingException {
        boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (keyUsage != null && !keyUsage[0] && !keyUsage[1]) {
            LOG.error("Certificate key usage does not include " +
                    "digitalSignature nor nonRepudiation");
        }

        List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
        if (extendedKeyUsage != null &&
                !extendedKeyUsage.contains(KeyPurposeId.id_kp_emailProtection.toString()) &&
                !extendedKeyUsage.contains(KeyPurposeId.id_kp_codeSigning.toString()) &&
                !extendedKeyUsage.contains(KeyPurposeId.anyExtendedKeyUsage.toString()) &&
                !extendedKeyUsage.contains("1.2.840.113583.1.1.5") &&
                !extendedKeyUsage.contains("1.3.6.1.4.1.311.10.3.12")) {
            LOG.error("Certificate extended key usage does not include " +
                    "emailProtection, nor codeSigning, nor anyExtendedKeyUsage, " +
                    "nor 'Adobe Authentic Documents Trust'");
        }
    }

    public static void checkTimeStampCertificateUsage(X509Certificate x509Certificate)
            throws CertificateParsingException {
        List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
        if (extendedKeyUsage != null &&
                !extendedKeyUsage.contains(KeyPurposeId.id_kp_timeStamping.toString())) {
            LOG.error("Certificate extended key usage does not include timeStamping");
        }
    }

    public static void checkResponderCertificateUsage(X509Certificate x509Certificate)
            throws CertificateParsingException {
        List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
        if (extendedKeyUsage != null &&
                !extendedKeyUsage.contains(KeyPurposeId.id_kp_OCSPSigning.toString())) {
            LOG.error("Certificate extended key usage does not include OCSP responding");
        }
    }

    public static PDSignature getLastRelevantSignature(PDDocument document) {
        Comparator<PDSignature> comparatorByOffset =
                Comparator.comparing(sig -> sig.getByteRange()[1]);
        Optional<PDSignature> optLastSignature =
                document.getSignatureDictionaries().stream().max(comparatorByOffset);
        if (optLastSignature.isPresent()) {
            PDSignature lastSignature = optLastSignature.get();
            COSBase type = lastSignature.getCOSObject().getItem(COSName.TYPE);
            if (type == null || COSName.SIG.equals(type) || COSName.DOC_TIME_STAMP.equals(type)) {
                return lastSignature;
            }
        }

        return null;
    }

    public static TimeStampToken extractTimeStampTokenFromSignerInformation(SignerInformation signerInformation)
            throws CMSException, IOException, TSPException {
        if (signerInformation.getUnsignedAttributes() == null) {
            return null;
        }

        AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        Attribute attribute = unsignedAttributes.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

        if (attribute == null) {
            return null;
        }

        ASN1Object obj = (ASN1Object) attribute.getAttrValues().getObjectAt(0);
        CMSSignedData signedTSTData = new CMSSignedData(obj.getEncoded());
        return new TimeStampToken(signedTSTData);
    }

    public static void validateTimestampToken(TimeStampToken timeStampToken)
            throws TSPException, CertificateException, OperatorCreationException, IOException {
        Collection<X509CertificateHolder> tstMatches =
                timeStampToken.getCertificates().getMatches(timeStampToken.getSID());
        X509CertificateHolder certificateHolder = tstMatches.iterator().next();
        SignerInformationVerifier siv =
                new JcaSimpleSignerInfoVerifierBuilder().setProvider(SecurityProvider.getProvider()).build(certificateHolder);
        timeStampToken.validate(siv);
    }

    public static void verifyCertificateChain(Store<X509CertificateHolder> certificatesStore,
                                              X509Certificate certFromSignedData, Date signDate)
            throws CertificateVerificationException, CertificateException {
        Collection<X509CertificateHolder> certificateHolders = certificatesStore.getMatches(null);
        Set<X509Certificate> additionalCerts = new HashSet<>();
        JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();

        for (X509CertificateHolder certHolder : certificateHolders) {
            X509Certificate certificate = certificateConverter.getCertificate(certHolder);
            if (!certificate.equals(certFromSignedData)) {
                additionalCerts.add(certificate);
            }
        }

        CertificateVerifier.verifyCertificate(certFromSignedData, additionalCerts, true, signDate);
    }

    public static X509Certificate getTsaCertificate(String tsaUrl)
            throws GeneralSecurityException, IOException, URISyntaxException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        TSAClient tsaClient = new TSAClient(new URI(tsaUrl).toURL(), null, null, digest);
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        TimeStampToken timeStampToken = tsaClient.getTimeStampToken(emptyStream);
        return getCertificateFromTimeStampToken(timeStampToken);
    }

    public static X509Certificate getCertificateFromTimeStampToken(TimeStampToken timeStampToken)
            throws CertificateException {
        Collection<X509CertificateHolder> tstMatches =
                timeStampToken.getCertificates().getMatches(timeStampToken.getSID());
        X509CertificateHolder tstCertHolder = tstMatches.iterator().next();
        return new JcaX509CertificateConverter().getCertificate(tstCertHolder);
    }

    public static void checkCrossReferenceTable(PDDocument doc) {
        TreeSet<COSObjectKey> set = new TreeSet<>(doc.getDocument().getXrefTable().keySet());
        if (set.size() != set.last().getNumber()) {
            long n = 0;
            for (COSObjectKey key : set) {
                ++n;
                while (n < key.getNumber()) {
                    LOG.warn("Object " + n + " missing, signature verification may fail in " +
                            "Adobe Reader, see https://stackoverflow.com/questions/71267471/");
                    ++n;
                }
            }
        }
    }

    public static InputStream openURL(String urlString) throws IOException, URISyntaxException {
        URL url = new URI(urlString).toURL();
        if (!urlString.startsWith("http")) {
            return url.openStream();
        }

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int responseCode = con.getResponseCode();
        LOG.info(responseCode + " " + con.getResponseMessage());

        if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
            String location = con.getHeaderField("Location");
            if (urlString.startsWith("http://") &&
                    location.startsWith("https://") &&
                    urlString.substring(7).equals(location.substring(8))) {
                LOG.info("redirection to " + location + " followed");
                con.disconnect();
                con = (HttpURLConnection) new URI(location).toURL().openConnection();
            } else {
                LOG.info("redirection to " + location + " ignored");
            }
        }
        return new ConnectedInputStream(con, con.getInputStream());
    }
}
