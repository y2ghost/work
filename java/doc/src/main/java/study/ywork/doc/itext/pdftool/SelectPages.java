package study.ywork.doc.itext.pdftool;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;

public class SelectPages {
    public static final String DEST = "selectPages_copy.pdf";
    private static final String MOVIE_TEMPLATES = "movieTemplates.pdf";

    public static void main(String[] args) throws IOException {
        new SelectPages().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        PdfReader reader = new PdfReader(MOVIE_TEMPLATES);
        manipulateWithCopy(4, 8, reader);
    }

    private static void manipulateWithCopy(int pageFrom, int pageTo, PdfReader reader) throws IOException {
        PdfDocument srcDoc = new PdfDocument(reader);
        PdfDocument copy = new PdfDocument(new PdfWriter(DEST));
        copy.initializeOutlines();
        srcDoc.copyPagesTo(pageFrom, pageTo, copy);
        copy.close();
        srcDoc.close();
    }
}
