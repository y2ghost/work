package study.ywork.doc.itext.position;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.FileNotFoundException;

public class GraphicsStateStack {
    private static final String DEST = "graphicsStateStack.pdf";

    public static void main(String[] args) throws Exception {
        new GraphicsStateStack().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) throws FileNotFoundException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        pdfDoc.setDefaultPageSize(new PageSize(200, 120));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFillColor(new DeviceRgb(0xff, 0x45, 0x00))
                .rectangle(10, 10, 60, 60) // 状态1
                .fill()
                .saveState()
                .setLineWidth(3) // 状态2
                .setFillColor(new DeviceRgb(0x8b, 0x00, 0x00))
                .rectangle(40, 20, 60, 60)
                .fillStroke()
                .saveState()
                .concatMatrix(1, 0, 0.1f, 1, 0, 0)   // 状态3
                .setStrokeColor(new DeviceRgb(0xff, 0x45, 0x00))
                .setFillColor(new DeviceRgb(0xff, 0xd7, 0x00))
                .rectangle(70, 30, 60, 60)
                .fillStroke()
                .restoreState()
                .rectangle(100, 40, 60, 60)
                .stroke()
                .restoreState()
                .rectangle(130, 50, 60, 60)
                .fillStroke();
        canvas.release();
        pdfDoc.close();
    }
}
