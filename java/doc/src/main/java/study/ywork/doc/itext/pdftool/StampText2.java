package study.ywork.doc.itext.pdftool;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class StampText2 {
    private static final String DEST = "stampText_Option2.pdf";
    private static final String SOURCE = "src/main/resources/pdfs/source.pdf";

    public static void main(String[] args) throws IOException {
        new StampText2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE);
             PdfWriter writer = new PdfWriter(dest);
             PdfDocument pdfDoc = new PdfDocument(reader, writer);
             Document doc = new Document(pdfDoc)) {
            Paragraph p = new Paragraph("Hello people!").setFixedPosition(7, 36, 540, 300);
            doc.add(p);
        }
    }
}
