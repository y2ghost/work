package study.ywork.doc.itext.pdftool;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;

public class Concatenate {
    private static final String DEST = "concatenate.pdf";
    private static final String MOVIE_LINKS1 = "src/main/resources/pdfs/movieLinks1.pdf";
    private static final String MOVIE_HISTORY = "src/main/resources/pdfs/movieHistory.pdf";

    public static void main(String[] args) throws IOException {
        new Concatenate().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        PdfDocument sourceDoc1 = new PdfDocument(new PdfReader(MOVIE_LINKS1));
        int n1 = sourceDoc1.getNumberOfPages();
        PdfDocument sourceDoc2 = new PdfDocument(new PdfReader(MOVIE_HISTORY));
        int n2 = sourceDoc2.getNumberOfPages();
        PdfDocument resultDoc = new PdfDocument(new PdfWriter(dest));

        for (int i = 1; i <= n1; i++) {
            PdfPage page = sourceDoc1.getPage(i).copyTo(resultDoc);
            resultDoc.addPage(page);
        }

        for (int i = 1; i <= n2; i++) {
            PdfPage page = sourceDoc2.getPage(i).copyTo(resultDoc);
            resultDoc.addPage(page);
        }
        
        resultDoc.close();
        sourceDoc1.close();
        sourceDoc2.close();
    }
}
