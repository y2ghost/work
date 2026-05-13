package study.ywork.doc.itext.page;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextMarginFinder;

import java.io.IOException;

public class ShowTextMargins {
    private static final String DEST = "showTextMargins.pdf";
    private static final String PREFACE = "src/main/resources/pdfs/preface.pdf";

    public static void main(String[] args) {
        new ShowTextMargins().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        addMarginRectangle(PREFACE, dest);
    }

    public void addMarginRectangle(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                TextMarginFinder finder = new TextMarginFinder();
                PdfCanvasProcessor parser = new PdfCanvasProcessor(finder);
                parser.processPageContent(pdfDoc.getPage(i));
                PdfCanvas cb = new PdfCanvas(pdfDoc.getPage(i));
                cb.rectangle(finder.getTextRectangle().getLeft(), finder.getTextRectangle().getBottom(),
                        finder.getTextRectangle().getWidth(), finder.getTextRectangle().getHeight());
                cb.stroke();
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
