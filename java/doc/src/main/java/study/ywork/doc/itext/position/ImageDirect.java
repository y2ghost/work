package study.ywork.doc.itext.position;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class ImageDirect {
    private static final String DEST = "imageDirect.pdf";
    private static final String RESOURCE = "src/main/resources/images/loa.jpg";

    public static void main(String[] args) {
        new ImageDirect().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        Rectangle postcard = new Rectangle(283, 416);
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(postcard.getWidth(), postcard.getHeight()))) {
            doc.setMargins(30, 30, 30, 30);
            Paragraph p = new Paragraph("Foobar Film Festival").setFontSize(22).setTextAlignment(TextAlignment.CENTER);
            doc.add(p);
            PdfImageXObject img = new PdfImageXObject(ImageDataFactory.create(RESOURCE));
            new PdfCanvas(pdfDoc.getLastPage()).addXObjectAt(img, (postcard.getWidth() - img.getWidth()) / 2, (postcard.getHeight() - img.getHeight()) / 2);
        } catch (MalformedURLException | FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
