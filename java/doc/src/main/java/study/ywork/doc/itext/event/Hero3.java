package study.ywork.doc.itext.event;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;

import java.io.IOException;

public class Hero3 extends Hero1 {
    private static final String DEST = "hero3.pdf";

    public static void main(String[] args) {
        new Hero3().manipulatePdf(DEST);
    }

    @Override
    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Rectangle art = new Rectangle(50, 50, 495, 742);
            pdfDoc.addNewPage().setArtBox(art);
            PdfFormXObject template = new PdfFormXObject(pdfDoc.getDefaultPageSize());
            PdfCanvas canvas = new PdfCanvas(template, pdfDoc);
            createTemplate(canvas, 1);
            canvas.stroke();
            new PdfCanvas(pdfDoc.getFirstPage()).addXObjectAt(template, 0, 0);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
