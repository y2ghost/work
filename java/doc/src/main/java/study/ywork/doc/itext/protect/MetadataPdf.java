package study.ywork.doc.itext.protect;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class MetadataPdf {
    private static final String[] RESULT = {
        "metadataPdf.pdf",
        "metadataPdf_pdf_metadata_changed.pdf"
    };

    public static final String DEST = RESULT[0];

    public static void main(String[] args) {
        new MetadataPdf().manipulatePdf();
    }

    public void manipulatePdf() {
        createPdf(DEST);
        changePdf(DEST, RESULT[1]);
    }

    public void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfDocumentInfo info = pdfDoc.getDocumentInfo();
            info.setTitle("Hello World example")
                .setAuthor("Bruno Lowagie")
                .setSubject("This example shows how to add metadata")
                .setKeywords("Metadata, iText, PDF")
                .setCreator("My program using iText");
            doc.add(new Paragraph("Hello World"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void changePdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfDocumentInfo info = pdfDoc.getDocumentInfo();
            info.setTitle("Hello World stamped");
            info.setSubject("Hello World with changed metadata");
            info.setKeywords("iText in Action, PdfStamper");
            info.setCreator("Silly standalone example");
            info.setAuthor("Also Bruno Lowagie");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
