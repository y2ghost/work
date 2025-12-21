package study.ywork.doc.itext.color;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfTransparencyGroup;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;

public class Transparency1 {
    private static final String DEST = "transparency1.pdf";

    public static void main(String[] args) {
        new Transparency1().manipulatePdf();
    }

    public void manipulatePdf() {
        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            float gap = (pdfDoc.getDefaultPageSize().getWidth() - 400) / 3;

            pictureBackdrop(gap, 500, canvas);
            pictureBackdrop(200 + 2 * gap, 500, canvas);
            pictureBackdrop(gap, 500 - 200 - gap, canvas);
            pictureBackdrop(200 + 2 * gap, 500 - 200 - gap, canvas);

            pictureCircles(gap, 500, canvas);
            canvas.saveState();
            PdfExtGState gs1 = new PdfExtGState();
            gs1.setFillOpacity(0.5f);
            canvas.setExtGState(gs1);
            pictureCircles(200 + 2 * gap, 500, canvas);
            canvas.restoreState();

            canvas.saveState();
            PdfFormXObject tp = new PdfFormXObject(new Rectangle(200, 200));
            PdfCanvas xObjectCanvas = new PdfCanvas(tp, pdfDoc);
            PdfTransparencyGroup transparencyGroup = new PdfTransparencyGroup();
            tp.setGroup(transparencyGroup);
            pictureCircles(0, 0, xObjectCanvas);
            canvas.setExtGState(gs1);
            canvas.addXObjectAt(tp, gap, 500 - 200 - gap);
            canvas.restoreState();

            canvas.saveState();
            tp = new PdfFormXObject(new Rectangle(200, 200));
            xObjectCanvas = new PdfCanvas(tp, pdfDoc);
            tp.setGroup(transparencyGroup);
            PdfExtGState gs2 = new PdfExtGState();
            gs2.setFillOpacity(0.5f);
            gs2.setBlendMode(PdfExtGState.BM_HARD_LIGHT);
            xObjectCanvas.setExtGState(gs2);
            pictureCircles(0, 0, xObjectCanvas);
            canvas.addXObjectAt(tp, 200 + 2 * gap, 500 - 200 - gap);
            canvas.restoreState();

            canvas.resetFillColorRgb();
            Paragraph[] params = {
                new Paragraph("Ungrouped objects\nObject opacity = 1.0"),
                new Paragraph("Ungrouped objects\nObject opacity = 0.5"),
                new Paragraph("Transparency group\nObject opacity = 1.0\nGroup opacity = 0.5\nBlend mode = Normal"),
                new Paragraph("Transparency group\nObject opacity = 0.5\nGroup opacity = 1.0\nBlend mode = HardLight"),
            };
            Rectangle[] rects = {
                new Rectangle(gap, 0, 200, 500),
                new Rectangle(200 + 2 * gap, 0, 200, 500),
                new Rectangle(gap, 0, 200, 500 - 200 - gap),
                new Rectangle(200 + 2 * gap, 0, 200, 500 - 200 - gap),
            };

            for (int i = 0; i < params.length; i++) {
                Paragraph p = params[i];
                Rectangle rect = rects[i];
                try (Canvas paraCanvas = new Canvas(canvas, rect)) {
                    paraCanvas.add(p.setTextAlignment(TextAlignment.CENTER).setFontSize(18));
                }
            }

            pdfDoc.close();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public static void pictureBackdrop(float x, float y, PdfCanvas canvas) {
        canvas.setStrokeColor(ColorConstants.DARK_GRAY)
            .setFillColor(new DeviceGray(0.8f))
            .rectangle(x, y, 100, 200)
            .fill()
            .setLineWidth(2)
            .rectangle(x, y, 200, 200)
            .stroke();
    }

    public static void pictureCircles(float x, float y, PdfCanvas canvas) {
        canvas.setFillColor(ColorConstants.RED)
            .circle(x + 70, y + 70, 50)
            .fill()
            .setFillColor(ColorConstants.YELLOW)
            .circle(x + 100, y + 130, 50)
            .fill()
            .setFillColor(ColorConstants.BLUE)
            .circle(x + 130, y + 70, 50)
            .fill();
    }
}
