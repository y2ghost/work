package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class LinkAnnotation {
    private static final String DEST = "link_annotation.pdf";

    public static void main(String[] args) throws IOException {
        new LinkAnnotation().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(0, 0))
                .setAction(PdfAction.createURI("https://wwww.baidu.com"));
        Link link = new Link("here", annotation);
        Paragraph p = new Paragraph("The example of link annotation. Click ")
                .add(link.setUnderline())
                .add(" to learn more...");
        document.add(p);
        document.close();
    }
}
