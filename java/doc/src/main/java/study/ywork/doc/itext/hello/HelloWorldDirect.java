package study.ywork.doc.itext.hello;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.IOException;

public class HelloWorldDirect {
    private static final String DEST = "helloWorldDirect.pdf";

    public static void main(String[] args) throws IOException {
        new HelloWorldDirect().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        try (PdfDocument pdfDoc = new PdfDocument(writer)) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.saveState()
                .beginText()
                .moveText(36, 600)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12)
                .showText("Hello World")
                .endText()
                .restoreState()
                .release();
        }
    }
}
