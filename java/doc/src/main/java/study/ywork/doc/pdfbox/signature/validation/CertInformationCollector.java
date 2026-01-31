package study.ywork.doc.pdfbox.signature.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.util.Store;
import study.ywork.doc.pdfbox.signature.SigUtils;
import study.ywork.doc.pdfbox.signature.cert.CertificateVerifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CertInformationCollector {
    private static final Log LOG = LogFactory.getLog(CertInformationCollector.class);
    private static final int MAX_CERTIFICATE_CHAIN_DEPTH = 5;
    private final Set<X509Certificate> certificateSet = new HashSet<>();
    private final Set<String> urlSet = new HashSet<>();
    private final JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
    private CertSignatureInformation rootCertInfo;

    public CertSignatureInformation getLastCertInfo(PDSignature signature, String fileName)
            throws CertificateProccessingException, IOException {
        try (FileInputStream documentInput = new FileInputStream(fileName)) {
            byte[] signatureContent = signature.getContents(documentInput);
            return getCertInfo(signatureContent);
        }
    }

    private CertSignatureInformation getCertInfo(byte[] signatureContent)
            throws CertificateProccessingException, IOException {
        rootCertInfo = new CertSignatureInformation();
        rootCertInfo.signatureHash = CertInformationHelper.getSha1Hash(signatureContent);

        try {
            CMSSignedData signedData = new CMSSignedData(signatureContent);
            SignerInformation signerInformation = processSignerStore(signedData, rootCertInfo);
            addTimestampCerts(signerInformation);
        } catch (CMSException e) {
            LOG.error("Error occurred getting Certificate Information from Signature", e);
            throw new CertificateProccessingException(e);
        }

        return rootCertInfo;
    }

    private void addTimestampCerts(SignerInformation signerInformation)
            throws IOException, CertificateProccessingException {
        AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        if (unsignedAttributes == null) {
            return;
        }

        Attribute tsAttribute = unsignedAttributes
                .get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
        if (tsAttribute == null) {
            return;
        }

        ASN1Encodable obj0 = tsAttribute.getAttrValues().getObjectAt(0);
        if (!(obj0 instanceof ASN1Object)) {
            return;
        }

        ASN1Object tsSeq = (ASN1Object) obj0;
        try {
            CMSSignedData signedData = new CMSSignedData(tsSeq.getEncoded("DER"));
            rootCertInfo.tsaCerts = new CertSignatureInformation();
            processSignerStore(signedData, rootCertInfo.tsaCerts);
        } catch (CMSException e) {
            throw new IOException("Error parsing timestamp token", e);
        }
    }

    private SignerInformation processSignerStore(
            CMSSignedData signedData, CertSignatureInformation certInfo)
            throws IOException, CertificateProccessingException {
        Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();
        SignerInformation signerInformation = signers.iterator().next();
        Store<X509CertificateHolder> certificatesStore = signedData.getCertificates();
        Collection<X509CertificateHolder> matches =
                certificatesStore.getMatches(signerInformation.getSID());
        X509Certificate certificate = getCertFromHolder(matches.iterator().next());
        certificateSet.add(certificate);
        Collection<X509CertificateHolder> allCerts = certificatesStore.getMatches(null);
        addAllCerts(allCerts);
        traverseChain(certificate, certInfo, MAX_CERTIFICATE_CHAIN_DEPTH);
        return signerInformation;
    }

    private void traverseChain(X509Certificate certificate, CertSignatureInformation certInfo,
                               int maxDepth) throws IOException, CertificateProccessingException {
        certInfo.certificate = certificate;
        byte[] authorityExtensionValue = certificate.getExtensionValue(Extension.authorityInfoAccess.getId());

        if (authorityExtensionValue != null) {
            CertInformationHelper.getAuthorityInfoExtensionValue(authorityExtensionValue, certInfo);
        }

        if (certInfo.issuerUrl != null) {
            getAlternativeIssuerCertificate(certInfo, maxDepth);
        }

        byte[] crlExtensionValue = certificate.getExtensionValue(Extension.cRLDistributionPoints.getId());
        if (crlExtensionValue != null) {
            certInfo.crlUrl = CertInformationHelper.getCrlUrlFromExtensionValue(crlExtensionValue);
        }

        certInfo.isSelfSigned = CertificateVerifier.isSelfSigned(certificate);
        if (maxDepth <= 0 || certInfo.isSelfSigned) {
            return;
        }

        int count = 0;
        for (X509Certificate issuer : certificateSet) {
            try {
                certificate.verify(issuer.getPublicKey(), SecurityProvider.getProvider());
                LOG.info("Found issuer for Cert: " + certificate.getSubjectX500Principal()
                        + "\n" + issuer.getSubjectX500Principal());
                certInfo.issuerCertificates.add(issuer);
                certInfo.certChain = new CertSignatureInformation();
                traverseChain(issuer, certInfo.certChain, maxDepth - 1);
                ++count;
            } catch (GeneralSecurityException ex) {
                // NOOP
            }
        }

        if (certInfo.issuerCertificates.isEmpty()) {
            throw new IOException(
                    "No Issuer Certificate found for Cert: '" +
                            certificate.getSubjectX500Principal() + "', i.e. Cert '" +
                            certificate.getIssuerX500Principal() + "' is missing in the chain");
        }

        if (count > 1) {
            LOG.info("Several issuers for Cert: '" + certificate.getSubjectX500Principal() + "'");
        }
    }

    private void getAlternativeIssuerCertificate(CertSignatureInformation certInfo, int maxDepth)
            throws CertificateProccessingException {
        if (urlSet.contains(certInfo.issuerUrl)) {
            return;
        }

        urlSet.add(certInfo.issuerUrl);
        LOG.info("Get alternative issuer certificate from: " + certInfo.issuerUrl);

        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            try (InputStream in = SigUtils.openURL(certInfo.issuerUrl)) {
                X509Certificate altIssuerCert = (X509Certificate) certFactory
                        .generateCertificate(in);
                certificateSet.add(altIssuerCert);
                certInfo.alternativeCertChain = new CertSignatureInformation();
                traverseChain(altIssuerCert, certInfo.alternativeCertChain, maxDepth - 1);
            }
        } catch (IOException | URISyntaxException | CertificateException e) {
            LOG.error("Error getting alternative issuer certificate from " + certInfo.issuerUrl, e);
        }
    }

    private X509Certificate getCertFromHolder(X509CertificateHolder certificateHolder)
            throws CertificateProccessingException {
        try {
            return certConverter.getCertificate(certificateHolder);
        } catch (CertificateException e) {
            LOG.error("Certificate Exception getting Certificate from certHolder.", e);
            throw new CertificateProccessingException(e);
        }
    }

    private void addAllCerts(Collection<X509CertificateHolder> certHolders) {
        for (X509CertificateHolder certificateHolder : certHolders) {
            try {
                X509Certificate certificate = getCertFromHolder(certificateHolder);
                certificateSet.add(certificate);
            } catch (CertificateProccessingException e) {
                LOG.warn("Certificate Exception getting Certificate from certHolder.", e);
            }
        }
    }

    public void addAllCertsFromHolders(X509CertificateHolder[] certHolders)
            throws CertificateProccessingException {
        addAllCerts(Arrays.asList(certHolders));
    }

    CertSignatureInformation getCertInfo(X509Certificate certificate) throws CertificateProccessingException {
        try {
            CertSignatureInformation certSignatureInformation = new CertSignatureInformation();
            traverseChain(certificate, certSignatureInformation, MAX_CERTIFICATE_CHAIN_DEPTH);
            return certSignatureInformation;
        } catch (IOException ex) {
            throw new CertificateProccessingException(ex);
        }
    }

    public Set<X509Certificate> getCertificateSet() {
        return certificateSet;
    }

    public static class CertSignatureInformation {
        private X509Certificate certificate;
        private String signatureHash;
        private boolean isSelfSigned = false;
        private String ocspUrl;
        private String crlUrl;
        private String issuerUrl;
        private final Set<X509Certificate> issuerCertificates = new HashSet<>();
        private CertSignatureInformation certChain;
        private CertSignatureInformation tsaCerts;
        private CertSignatureInformation alternativeCertChain;

        public String getOcspUrl() {
            return ocspUrl;
        }

        public void setOcspUrl(String ocspUrl) {
            this.ocspUrl = ocspUrl;
        }

        public void setIssuerUrl(String issuerUrl) {
            this.issuerUrl = issuerUrl;
        }

        public String getCrlUrl() {
            return crlUrl;
        }

        public X509Certificate getCertificate() {
            return certificate;
        }

        public boolean isSelfSigned() {
            return isSelfSigned;
        }

        public Set<X509Certificate> getIssuerCertificates() {
            return issuerCertificates;
        }

        public String getSignatureHash() {
            return signatureHash;
        }

        public CertSignatureInformation getCertChain() {
            return certChain;
        }

        public CertSignatureInformation getTsaCerts() {
            return tsaCerts;
        }

        public CertSignatureInformation getAlternativeCertChain() {
            return alternativeCertChain;
        }
    }
}
