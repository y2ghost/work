package study.ywork.doc.itext.pdftool;

import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;

public class ConcatenateForms1 {
    private static final String DATASHEET = "src/main/resources/pdfs/datasheet.pdf";
    private static final String DEST = "concatenateForms1.pdf";

    public static void main(String[] args) throws IOException {
        new ConcatenateForms1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            pdfDoc.initializeOutlines();
            PdfPageFormCopier formCopier = new PdfPageFormCopier();
            try (PdfDocument srcDoc = new PdfDocument(new PdfReader(DATASHEET))) {
                srcDoc.copyPagesTo(1, 1, pdfDoc, formCopier);
            }

            try (PdfDocument srcDoc = new PdfDocument(new PdfReader(DATASHEET))) {
                srcDoc.copyPagesTo(1, 1, pdfDoc, formCopier);
            }
        }
    }
}
