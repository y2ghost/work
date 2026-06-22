package study.ywork.doc.itext.inside;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class PrintPreferencesExample {
    private static final String DEST = "printPreferencesExample.pdf";

    public static void main(String[] args) {
        new PrintPreferencesExample().manipulatePdf(DEST);
    }

    private void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(PdfVersion.PDF_1_5)));
             Document doc = new Document(pdfDoc)) {
            PdfViewerPreferences prefs = new PdfViewerPreferences();
            prefs.setPrintScaling(PdfViewerPreferences.PdfViewerPreferencesConstants.NONE);
            prefs.setNumCopies(3);
            prefs.setPickTrayByPDFSize(true);
            pdfDoc.getCatalog().setViewerPreferences(prefs);
            doc.add(new Paragraph("Hello World"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
