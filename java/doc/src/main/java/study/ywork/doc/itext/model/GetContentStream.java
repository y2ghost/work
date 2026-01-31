package study.ywork.doc.itext.model;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;

public class GetContentStream {
    private static final String DEST = "getContentStream1.txt";
    private static final String RESULT2 = "getContentStream2.txt";
    private static final String HELLO_WORLD = "src/main/resources/pdfs/HelloWorld.pdf";
    private static final String HERO = "src/main/resources/pdfs/hero.pdf";

    public static void main(String[] args) {
        new GetContentStream().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        readContent(HELLO_WORLD, dest);
        readContent(HERO, RESULT2);
    }

    private void readContent(String src, String result) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src));
             FileOutputStream out = new FileOutputStream(result)) {
            out.write(pdfDoc.getFirstPage().getContentBytes());
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
