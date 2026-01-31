package study.ywork.doc.itext.stream;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Document;

import java.io.IOException;

public class SpecialId {
    private static final String DEST = "specialId.pdf";
    private static final String RESOURCE = "src/main/resources/images/bruno.jpg";

    public static void main(String[] args) {
        new SpecialId().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(new Rectangle(400, 300)))) {
            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(RESOURCE));
            xObject.put(new PdfName("ITXT_SpecialId"), new PdfName("123456789"));
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.addXObjectAt(xObject, 0, 0);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
