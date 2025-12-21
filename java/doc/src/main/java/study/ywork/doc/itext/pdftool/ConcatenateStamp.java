package study.ywork.doc.itext.pdftool;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class ConcatenateStamp {
    private static final String DEST = "concatenateStamp.pdf";
    private static final String MOVIE_LINKS1 = "src/main/resources/pdfs/movieLinks1.pdf";
    private static final String MOVIE_HISTORY = "src/main/resources/pdfs/movieHistory.pdf";

    public static void main(String[] args) throws IOException {
        new ConcatenateStamp().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        try (PdfDocument resultDoc = new PdfDocument(new PdfWriter(DEST));
             Document doc = new Document(resultDoc);
             PdfDocument srcDoc1 = new PdfDocument(new PdfReader(MOVIE_LINKS1));
             PdfDocument srcDoc2 = new PdfDocument(new PdfReader(MOVIE_HISTORY))) {
            int n1 = srcDoc1.getNumberOfPages();
            int n2 = srcDoc2.getNumberOfPages();
            resultDoc.addNewPage();

            for (int i = 1; i <= n1; i++) {
                PdfPage page = resultDoc.getLastPage();
                PdfFormXObject backPage = srcDoc1.getPage(i).copyAsFormXObject(resultDoc);
                new PdfCanvas(page.newContentStreamBefore(), page.getResources(), resultDoc)
                        .addXObjectAt(backPage, 0, 0);
                doc.add(new Paragraph(String.format("page %d of %d", i, n1 + n2)).setMargin(0).setMultipliedLeading(1).setFixedPosition(297.5f, 28, 200));
                doc.add(new AreaBreak());
            }

            for (int i = 1; i <= n2; i++) {
                PdfFormXObject backPage = srcDoc2.getPage(i).copyAsFormXObject(resultDoc);
                PdfPage page = resultDoc.getLastPage();
                new PdfCanvas(page.newContentStreamBefore(), page.getResources(), resultDoc)
                        .addXObjectAt(backPage, 0, 0);
                doc.add(new Paragraph(String.format("page %d of %d", i + n1, n1 + n2))
                        .setMargin(0)
                        .setMultipliedLeading(1)
                        .setFixedPosition(297.5f, 28, 200));

                if (n2 != i) {
                    doc.add(new AreaBreak());
                }
            }
        }
    }
}
