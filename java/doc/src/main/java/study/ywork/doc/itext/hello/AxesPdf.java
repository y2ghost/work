package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

import java.io.IOException;

/**
 * 简单的坐标画线示例
 */
public class AxesPdf {
    private static final String DEST = "axes.pdf";

    public static void main(String[] args) throws IOException {
        new AxesPdf().createPdf(DEST);
    }

    public void createPdf(String outFile) throws IOException {
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(outFile))) {
            PageSize ps = PageSize.A4.rotate();
            PdfPage page = pdf.addNewPage(ps);
            PdfCanvas canvas = new PdfCanvas(page);
            // 将坐标系统的原点替换为页面的中心
            canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);
            AxesPdf.drawAxes(canvas, ps);
        }
    }

    public static void drawAxes(PdfCanvas canvas, PageSize ps) {
        // 绘制X轴
        canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .stroke();

        // 绘制X轴箭头
        canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(ps.getWidth() / 2 - 25, -10)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .lineTo(ps.getWidth() / 2 - 25, 10).stroke()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);

        // 绘制Y轴
        canvas.moveTo(0, -(ps.getHeight() / 2 - 15))
                .lineTo(0, ps.getHeight() / 2 - 15)
                .stroke();

        // 绘制Y轴箭头
        canvas.saveState()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(-10, ps.getHeight() / 2 - 25)
                .lineTo(0, ps.getHeight() / 2 - 15)
                .lineTo(10, ps.getHeight() / 2 - 25).stroke()
                .restoreState();

        // 绘制X坐标线
        for (int i = -((int) ps.getWidth() / 2 - 61); i < ((int) ps.getWidth() / 2 - 60); i += 40) {
            canvas.moveTo(i, 5).lineTo(i, -5);
        }

        // 绘制Y做表现
        for (int j = -((int) ps.getHeight() / 2 - 57); j < ((int) ps.getHeight() / 2 - 56); j += 40) {
            canvas.moveTo(5, j).lineTo(-5, j);
        }

        canvas.stroke();
    }
}
