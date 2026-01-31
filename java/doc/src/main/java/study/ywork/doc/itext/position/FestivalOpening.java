package study.ywork.doc.itext.position;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;

public class FestivalOpening {

    private static final String DEST = "festivalOpening.pdf";
    private static final String RESOURCE = "src/main/resources/images/loa.jpg";
    private static final float PAGE_WIDTH = PageSize.A6.getWidth();
    private static final float PAGE_HEIGHT = PageSize.A6.getHeight();

    public static void main(String[] args) throws IOException {
        new FestivalOpening().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, PageSize.A6)) {
            Paragraph p = new Paragraph("Foobar Film Festival").
                    setTextAlignment(TextAlignment.CENTER).
                    setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).
                    setFontSize(22);

            PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(RESOURCE));
            Image img = new Image(imageXObject);
            img.setFixedPosition((PAGE_WIDTH - imageXObject.getWidth()) / 2, (PAGE_HEIGHT - imageXObject.getHeight()) / 2);
            doc.add(img).add(p);

            // AreaBreak用于移动到下一页
            doc.add(new AreaBreak()).add(img).add(p);
            PdfCanvas canvas = new PdfCanvas(pdfDoc.getLastPage());
            float sine = (float) Math.sin(Math.PI / 60);
            float cosine = (float) Math.cos(Math.PI / 60);
            canvas.saveState()
                    .beginText()
                    .setTextRenderingMode(2).
                    setLineWidth(1.5f)
                    .setFillColor(ColorConstants.WHITE)
                    .setStrokeColor(ColorConstants.RED).
                    setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 36)
                    .setTextMatrix(cosine, sine, -sine, cosine, 50, 324).
                    showText("SOLD OUT")
                    .endText()
                    .restoreState();
            PdfCanvas underCanvas = new PdfCanvas(pdfDoc.getLastPage().newContentStreamBefore(), pdfDoc.getLastPage().getResources(), pdfDoc);
            underCanvas.saveState()
                    .setFillColor(new DeviceRgb(0xFF, 0xD7, 0x00))
                    .rectangle(5, 5, PAGE_WIDTH - 10, PAGE_HEIGHT - 10)
                    .fill()
                    .restoreState();
            canvas.release();
        }
    }
}
