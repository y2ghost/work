package study.ywork.doc.itext.pdftool;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.IOException;

public class Superimposing {
    private static final String DEST = "superimposing.pdf";
    private static final String SOURCE = "superimposing_opening.pdf";
    private static final String RESOURCE = "src/main/resources/images/loa.jpg";
    private static final PageSize POST_CARD = new PageSize(283, 416);

    public static void main(String[] args) throws IOException {
        new Superimposing().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        createOriginalPdf(SOURCE);
        try (PdfDocument srcDoc = new PdfDocument(new PdfReader(SOURCE));
             PdfDocument resultDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(resultDoc, POST_CARD)) {
            PdfCanvas canvas = new PdfCanvas(resultDoc.addNewPage());
            for (int i = 1; i <= srcDoc.getNumberOfPages(); i++) {
                PdfFormXObject layer = srcDoc.getPage(i).copyAsFormXObject(resultDoc);
                canvas.addXObjectWithTransformationMatrix(layer, 1f, 0, 0, 1, 0, 0);
            }
        }
    }

    public void createOriginalPdf(String filename) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
             Document doc = new Document(pdfDoc, POST_CARD)) {
            doc.setMargins(30, 30, 30, 30);

            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas under = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
            under.setFillColor(new DeviceRgb(0xFF, 0xD7, 0x00));
            under.rectangle(5, 5, POST_CARD.getWidth() - 10, POST_CARD.getHeight() - 10);
            under.fillStroke();
            doc.add(new AreaBreak());

            Image img = new Image(ImageDataFactory.create(RESOURCE));
            img.setFixedPosition((POST_CARD.getWidth() - img.getImageScaledWidth()) / 2,
                    (POST_CARD.getHeight() - img.getImageScaledHeight()) / 2);
            doc.add(img);
            doc.add(new AreaBreak());

            Paragraph p = new Paragraph("Foobar Film Festival")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(22)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            doc.add(p);
            doc.add(new AreaBreak());

            page = pdfDoc.getLastPage();
            PdfCanvas over = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
            over.saveState();
            float sinus = (float) Math.sin(Math.PI / 60);
            float cosinus = (float) Math.cos(Math.PI / 60);
            over.beginText();
            over.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
            over.setLineWidth(1.5f);
            over.setStrokeColor(new DeviceRgb(0xFF, 0x00, 0x00));
            over.setFillColor(new DeviceRgb(0xFF, 0xFF, 0xFF));
            over.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 36);
            over.setTextMatrix(cosinus, sinus, -sinus, cosinus, 50, 324);
            over.showText("SOLD OUT");
            over.setTextMatrix(0, 0);
            over.endText();
            over.restoreState();
        }
    }
}
