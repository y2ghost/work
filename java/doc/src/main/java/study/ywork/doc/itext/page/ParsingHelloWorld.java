package study.ywork.doc.itext.page;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ParsingHelloWorld {
    private static final String DEST = "parsingHelloWorld.pdf";
    private static final String[] TEXT = {
            "parsingHelloWorld1.txt",
            "parsingHelloWorld2.txt",
            "parsingHelloWorld3.txt"
    };
    private static final String HELLO_WORLD = "src/main/resources/pdfs/HelloWorld.pdf";

    public void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12);
            canvas.moveText(88.66f, 367);
            canvas.showText("ld");
            canvas.moveText(-22f, 0);
            canvas.showText("Wor");
            canvas.moveText(-15.33f, 0);
            canvas.showText("llo");
            canvas.moveText(-15.33f, 0);
            canvas.showText("He");
            canvas.endText();
            PdfFormXObject xObject = new PdfFormXObject(new Rectangle(250, 25));
            PdfCanvas xObjectCanvas = new PdfCanvas(xObject, pdfDoc);
            xObjectCanvas.beginText();
            xObjectCanvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12);
            xObjectCanvas.moveText(0, 7);
            xObjectCanvas.showText("Hello People");
            xObjectCanvas.endText();
            canvas.addXObjectAt(xObject, 36, 343);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void parsePdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src));
             PrintWriter out = new PrintWriter(new FileOutputStream(dest))) {
            byte[] streamBytes = pdfDoc.getFirstPage().getContentBytes();
            try (PdfTokenizer tokenizer = new PdfTokenizer(new RandomAccessFileOrArray(
                    new RandomAccessSourceFactory().createSource(streamBytes)))) {
                while (tokenizer.nextToken()) {
                    if (tokenizer.getTokenType() == PdfTokenizer.TokenType.String) {
                        out.println(tokenizer.getStringValue());
                    }
                }
            }
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void extractText(String src, String dest) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(dest));
             PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            IEventListener listener = new MyTextRenderListener(out);
            PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
            processor.processPageContent(pdfDoc.getFirstPage());
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ParsingHelloWorld().manipulatePdf();
    }

    public void manipulatePdf() {
        createPdf(DEST);
        parsePdf(HELLO_WORLD, TEXT[0]);
        parsePdf(DEST, TEXT[1]);
        extractText(DEST, TEXT[2]);
    }
}
