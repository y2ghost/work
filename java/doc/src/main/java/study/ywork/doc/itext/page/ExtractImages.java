package study.ywork.doc.itext.page;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

import java.io.IOException;

public class ExtractImages {
    public static final String DEST = "extractedImg%s.%s";
    public static final String IMAGE_TYPES = "src/main/resources/pdfs/ImageTypes.pdf";

    public static void main(String[] args) {
        new ExtractImages().manipulatePdf();
    }

    private void manipulatePdf() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(IMAGE_TYPES), new PdfWriter(new ByteArrayOutputStream()))) {
            IEventListener listener = new MyImageRenderListener(DEST);
            PdfCanvasProcessor parser = new PdfCanvasProcessor(listener);
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                parser.processPageContent(pdfDoc.getPage(i));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
