package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileNotFoundException;

public class NewPage {
    private static final String DEST = "newPage.pdf";

    public static void main(String[] args) {
        new NewPage().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            doc.add(new Paragraph("This page will NOT be followed by a blank page!"));
            doc.add(new AreaBreak());
            doc.add(new Paragraph("This page will be followed by a blank page!"));
            doc.add(new AreaBreak());
            doc.add(new AreaBreak());
            doc.add(new Paragraph("The previous page was a blank page!"));
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
