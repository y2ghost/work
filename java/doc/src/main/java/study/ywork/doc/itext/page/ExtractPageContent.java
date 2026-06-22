package study.ywork.doc.itext.page;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ExtractPageContent {
    private static final String DEST = "extractPageContent.txt";
    private static final String PREFACE = "src/main/resources/pdfs/preface.pdf";

    public static void main(String[] args) {
        new ExtractPageContent().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        parsePdf(PREFACE, dest);
    }

    public void parsePdf(String src, String txt) {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(txt), StandardCharsets.UTF_8));
             PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                parser.processPageContent(pdfDoc.getPage(i));
                out.println(strategy.getResultantText());
            }
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
