package study.ywork.doc.itext.page;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.filter.IEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ExtractPageContentArea {
    private static final String DEST = "extractPageContentArea.txt";
    private static final String PREFACE = "src/main/resources/pdfs/preface.pdf";

    public static void main(String[] args) {
        new ExtractPageContentArea().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        parsePdf(PREFACE, dest);
    }

    private void parsePdf(String src, String txt) {
        Rectangle rect = new Rectangle(70, 80, 420, 500);
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(txt), StandardCharsets.UTF_8));
             PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(new ByteArrayOutputStream()))) {
            IEventFilter filter = new TextRegionEventFilter(rect);
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                ITextExtractionStrategy strategy = new FilteredTextEventListener(new LocationTextExtractionStrategy(), filter);
                out.println(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i), strategy));
            }
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
