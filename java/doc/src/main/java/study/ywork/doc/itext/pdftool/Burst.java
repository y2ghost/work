package study.ywork.doc.itext.pdftool;

import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;

public class Burst {
    private static final String DEST = "burst.pdf";
    public static final String FORMATTED_DEST = "burst%d.pdf";
    public static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/movieTemplates.pdf";

    public static void main(String[] args) throws IOException {
        new Burst().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        String[] names;
        try (PdfDocument srcDoc = new PdfDocument(new PdfReader(MOVIE_TEMPLATES))) {
            PdfPageFormCopier formCopier = new PdfPageFormCopier();

            for (int i = 1; i <= srcDoc.getNumberOfPages(); i++) {
                PdfDocument pdfDoc = new PdfDocument(new PdfWriter(String.format(FORMATTED_DEST, i)));
                pdfDoc.initializeOutlines();
                srcDoc.copyPagesTo(i, i, pdfDoc, formCopier);
                pdfDoc.close();
            }

            names = new String[srcDoc.getNumberOfPages()];
            for (int i = 0; i < names.length; i++) {
                names[i] = String.format(FORMATTED_DEST, i + 1);
            }
        }

        concatenateResults(DEST, names);
    }

    protected void concatenateResults(String dest, String[] names) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            pdfDoc.initializeOutlines();
            for (String name : names) {
                try (PdfDocument tempDoc = new PdfDocument(new PdfReader(name))) {
                    tempDoc.copyPagesTo(1, tempDoc.getNumberOfPages(), pdfDoc);
                }
            }
        }
    }
}
