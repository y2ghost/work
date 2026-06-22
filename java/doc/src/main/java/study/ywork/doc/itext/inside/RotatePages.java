package study.ywork.doc.itext.inside;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;

public class RotatePages {
    private static final String DEST = "rotatePages.pdf";
    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/MovieTemplates.pdf";

    public static void main(String[] args) {
        new RotatePages().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        changePdf(MOVIE_TEMPLATES, dest);
    }

    public void changePdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                int rot = pdfDoc.getPage(i).getRotation();
                PdfDictionary pageDict = pdfDoc.getPage(i).getPdfObject();
                pageDict.put(PdfName.Rotate, new PdfNumber(rot + 90));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
