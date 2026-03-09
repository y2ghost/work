package study.ywork.doc.itext.inside;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;

public class CropPages {
    private static final String DEST = "cropPages.pdf";
    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/MovieTemplates.pdf";

    public static void main(String[] args) {
        new CropPages().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        changePdf(MOVIE_TEMPLATES, dest);
    }

    private void changePdf(String src, String dest) {
        PdfArray rect = new PdfArray(new int[]{55, 76, 560, 816});
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfDictionary pageDict = pdfDoc.getPage(i).getPdfObject();
                pageDict.put(PdfName.CropBox, rect);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
