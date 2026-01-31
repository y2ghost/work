package study.ywork.doc.itext.protect;

import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * 创建keystore，示例输入密码 f00b4r
 * keytool -genkey -alias foobar -keyalg RSA -keystore .keystore
 * keytool -export -alias foobar -file foobar.cer -keystore .keystore
 */
public class EncryptWithCertificate {
    private static final String RESULT1 = "encryptWithCertificate_certificate_encryption.pdf";
    private static final String DEST = "encryptWithCertificate_certificate_decrypted.pdf";
    private static final String RESULT3 = "encryptWithCertificate_certificate_encrypted.pdf";
    private static final String KEYSTORE = "src/main/resources/encryption/.keystore";
    private static final String PUBLIC_KEY = "src/main/resources/encryption/foobar.cer";

    public static void main(String[] args) {
        new EncryptWithCertificate().manipulatePdf();
    }

    private void manipulatePdf() {
        Security.addProvider(new BouncyCastleProvider());
        createPdf(RESULT1);
        decryptPdf(RESULT1, DEST);
        encryptPdf(DEST, RESULT3);
    }

    public void createPdf(String dest) {
        try {
            Certificate cert = getPublicCertificate(PUBLIC_KEY);
            PdfWriter writer = new PdfWriter(dest, new WriterProperties().setPublicKeyEncryption(new Certificate[]{cert},
                new int[]{EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ALLOW_COPY}, EncryptionConstants.ENCRYPTION_AES_128));
            try (Document doc = new Document(new PdfDocument(writer))) {
                doc.add(new Paragraph("Hello World!"));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private Certificate getPublicCertificate(String path) {
        Certificate cert = null;
        try (FileInputStream is = new FileInputStream(path)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = cf.generateCertificate(is);
        } catch (IOException | CertificateException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        return cert;
    }

    private PrivateKey getPrivateKey() throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(KEYSTORE), "f00b4r".toCharArray());
        return (PrivateKey) ks.getKey("foobar", "f00b4r".toCharArray());
    }

    private void decryptPdf(String src, String dest) {
        try {
            ReaderProperties readerProperties = new ReaderProperties()
                .setPublicKeySecurityParams(getPublicCertificate(PUBLIC_KEY),
                    getPrivateKey(), "BC", null);
            try (PdfReader reader = new PdfReader(new RandomAccessSourceFactory().createBestSource(src),
                readerProperties);
                 PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(dest))) {
                // NOOP
            }
        } catch (IOException | GeneralSecurityException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void encryptPdf(String src, String dest) {
        try {
            Certificate cert = getPublicCertificate(PUBLIC_KEY);
            try (PdfReader reader = new PdfReader(src);
                 PdfWriter writer = new PdfWriter(dest, new WriterProperties()
                     .setPublicKeyEncryption(new Certificate[]{cert},
                         new int[]{EncryptionConstants.ALLOW_PRINTING}, EncryptionConstants.ENCRYPTION_AES_128));
                 PdfDocument pdfDoc = new PdfDocument(reader, writer)) {
                // NOOP
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
