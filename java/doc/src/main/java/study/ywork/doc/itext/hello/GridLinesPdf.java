package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.IOException;

/**
 * 绘制线条示例
 */
public class GridLinesPdf {
    private static final String DEST = "grid_lines.pdf";

    public static void main(String[] args) throws IOException {
        new GridLinesPdf().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);
        Color grayColor = new DeviceCmyk(0.f, 0.f, 0.f, 0.875f);
        Color greenColor = new DeviceCmyk(1.f, 0.f, 1.f, 0.176f);
        Color blueColor = new DeviceCmyk(1.f, 0.156f, 0.f, 0.118f);

        canvas.setLineWidth(0.5f).setStrokeColor(blueColor);
        for (int i = -((int) ps.getHeight() / 2 - 57); i < ((int) ps.getHeight() / 2 - 56); i += 40) {
            canvas.moveTo(-(ps.getWidth() / 2 - 15), i).lineTo(ps.getWidth() / 2 - 15, i);
        }

        for (int j = -((int) ps.getWidth() / 2 - 61); j < ((int) ps.getWidth() / 2 - 60); j += 40) {
            canvas.moveTo(j, -(ps.getHeight() / 2 - 15)).lineTo(j, ps.getHeight() / 2 - 15);
        }

        canvas.stroke();
        canvas.setLineWidth(3).setStrokeColor(grayColor);
        AxesPdf.drawAxes(canvas, ps);

        canvas.setLineWidth(2).setStrokeColor(greenColor)
                .setLineDash(10, 10, 8)
                .moveTo(-(ps.getWidth() / 2 - 15), -(ps.getHeight() / 2 - 15))
                .lineTo(ps.getWidth() / 2 - 15, ps.getHeight() / 2 - 15)
                .stroke();
        pdf.close();
    }
}
