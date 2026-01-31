package study.ywork.doc.itext.protect;

import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class EncryptionPdf {
    private static final byte[] USER = "Hello".getBytes();
    private static final byte[] OWNER = "World".getBytes();
    private static final String[] RESULT = {
        "encryptionPdf_encryption.pdf",
        "encryptionPdf_encryption_decrypted.pdf",
        "encryptionPdf_encryption_encrypted.pdf"
    };

    public static void main(String[] args) {
        new EncryptionPdf().manipulatePdf();
    }

    public void manipulatePdf() {
        createPdf(RESULT[0]);
        decryptPdf(RESULT[0], RESULT[1]);
        encryptPdf(RESULT[1], RESULT[2]);
    }

    private void createPdf(String dest) {
        try (PdfWriter writer = new PdfWriter(dest, new WriterProperties()
            .setStandardEncryption(USER, OWNER,
                EncryptionConstants.ALLOW_PRINTING,
                EncryptionConstants.STANDARD_ENCRYPTION_128));
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph("Hello World"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void decryptPdf(String src, String dest) {
        try (PdfReader reader = new PdfReader(src, new ReaderProperties().setPassword(OWNER));
             PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(dest))) {
            // NOOP
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void encryptPdf(String src, String dest) {
        try (PdfWriter writer = new PdfWriter(dest, new WriterProperties()
            .setStandardEncryption(USER, OWNER,
                EncryptionConstants.ALLOW_PRINTING,
                EncryptionConstants.ENCRYPTION_AES_128 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA));
             PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), writer)) {
            // NOOP
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
