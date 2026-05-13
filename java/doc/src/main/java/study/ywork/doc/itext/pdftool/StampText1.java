package study.ywork.doc.itext.pdftool;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.IOException;

public class StampText1 {
    private static final String DEST = "stampText_Option1.pdf";
    private static final String SOURCE = "src/main/resources/pdfs/source.pdf";

    public static void main(String[] args) throws IOException {
        new StampText1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE);
             PdfWriter writer = new PdfWriter(dest);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
            canvas.saveState().beginText().setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12).
                moveText(36, 540).showText("Hello people!").endText().restoreState();
            canvas.release();
        }
    }
}
