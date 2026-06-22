package study.ywork.doc.itext.event;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;

import java.io.FileReader;
import java.io.IOException;

public class Hero1 {
    private static final String DEST = "hero1.pdf";
    private static final String RESOURCE = "./src/main/resources/txt/hero.txt";

    public static void main(String[] args) {
        new Hero1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        Rectangle rectangle = new Rectangle(-1192F, -1685F, 2F * 1192, 2F * 1685);
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document ignored = new Document(pdfDoc, new PageSize(rectangle))) {
            pdfDoc.addNewPage();
            PdfFormXObject template = new PdfFormXObject(new Rectangle(pdfDoc.getDefaultPageSize().getWidth(),
                    pdfDoc.getDefaultPageSize().getHeight()));
            PdfCanvas canvasTemplate = new PdfCanvas(template, pdfDoc);
            createTemplate(canvasTemplate, 4);
            PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage()).addXObjectAt(template, -1192, -1685);
            canvas.moveTo(-595, 0);
            canvas.lineTo(595, 0);
            canvas.moveTo(0, -842);
            canvas.lineTo(0, 842);
            canvas.stroke();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void createTemplate(PdfCanvas canvas, int factor) throws IOException {
        canvas.concatMatrix(factor, 0, 0, factor, 0, 0);
        try (FileReader reader = new FileReader(RESOURCE)) {
            while (true) {
                int c = reader.read();
                if (c < 0) {
                    break;
                }

                canvas.writeLiteral(String.valueOf((char) c));
            }
        }
    }
}
