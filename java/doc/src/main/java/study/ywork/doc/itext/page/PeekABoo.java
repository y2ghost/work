package study.ywork.doc.itext.page;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;

import java.io.IOException;

public class PeekABoo {
    private static final String[] RESULT = {
            "pickABoo.pdf",
            "pickABoo2.pdf"
    };

    private static final String DEST = RESULT[0];

    public static void main(String[] args) {
        new PeekABoo().manipulatePdf();
    }

    public void manipulatePdf() {
        createPdf(DEST, true);
        createPdf(RESULT[1], false);
    }

    public void createPdf(String dest, boolean on) {
        try (PdfWriter writer = new PdfWriter(dest, new WriterProperties().setPdfVersion(PdfVersion.PDF_1_5));
             PdfDocument pdfDoc = new PdfDocument(writer)) {
            pdfDoc.getCatalog().setPageLayout(PdfName.UseOC);
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            PdfLayer layer = new PdfLayer("Do you see me?", pdfDoc);
            layer.setOn(on);
            canvas.beginText().setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18)
                    .moveText(50, 760)
                    .showText("Do you see me?")
                    .beginLayer(layer)
                    .moveText(0, -30)
                    .showText("Peek-A-Boo!!!")
                    .endLayer()
                    .endText()
                    .release();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
