package study.ywork.doc.pdfbox.signature;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

public abstract class CreateSignatureBase implements SignatureInterface {
    private PrivateKey privateKey;
    private Certificate[] certificateChain;
    private String tsaUrl;
    private boolean externalSigning;

    protected CreateSignatureBase(KeyStore keystore, char[] pin)
            throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {
        Enumeration<String> aliases = keystore.aliases();
        String alias;
        Certificate cert = null;

        while (cert == null && aliases.hasMoreElements()) {
            alias = aliases.nextElement();
            setPrivateKey((PrivateKey) keystore.getKey(alias, pin));
            Certificate[] certChain = keystore.getCertificateChain(alias);
            if (certChain != null) {
                setCertificateChain(certChain);
                cert = certChain[0];

                if (cert instanceof X509Certificate x509Certificate) {
                    x509Certificate.checkValidity();
                    SigUtils.checkCertificateUsage(x509Certificate);
                }
            }
        }

        if (cert == null) {
            throw new IOException("Could not find certificate");
        }
    }

    public final void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public final void setCertificateChain(final Certificate[] certificateChain) {
        this.certificateChain = certificateChain;
    }

    public Certificate[] getCertificateChain() {
        return certificateChain;
    }

    public void setTsaUrl(String tsaUrl) {
        this.tsaUrl = tsaUrl;
    }

    @Override
    public byte[] sign(InputStream content) throws IOException {
        try {
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            X509Certificate cert = (X509Certificate) certificateChain[0];
            ContentSigner sha1Signer = new JcaContentSignerBuilder(cert.getSigAlgName()).build(privateKey);
            gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, cert));
            gen.addCertificates(new JcaCertStore(Arrays.asList(certificateChain)));
            CMSProcessableInputStream msg = new CMSProcessableInputStream(content);
            CMSSignedData signedData = gen.generate(msg, false);

            if (tsaUrl != null && !tsaUrl.isEmpty()) {
                ValidationTimeStamp validation = new ValidationTimeStamp(tsaUrl);
                signedData = validation.addSignedTimeStamp(signedData);
            }

            return signedData.getEncoded();
        } catch (GeneralSecurityException | CMSException | OperatorCreationException | URISyntaxException e) {
            throw new IOException(e);
        }
    }

    public void setExternalSigning(boolean externalSigning) {
        this.externalSigning = externalSigning;
    }

    public boolean isExternalSigning() {
        return externalSigning;
    }
}
