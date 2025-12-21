package study.ywork.doc.itext.model;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.io.IOException;

public class TransformationMatrix1 {
    private static final String DEST = "transformationMatrix1.pdf";
    private static final String RESOURCE = "src/main/resources/pdfs/logo.pdf";

    public static void main(String[] args) {
        new TransformationMatrix1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             PdfDocument srcDoc = new PdfDocument(new PdfReader(RESOURCE))) {
            pdfDoc.setDefaultPageSize(new PageSize(new Rectangle(-595, -842, 595F * 2, 842F * 2)));

            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.moveTo(-595, 0)
                    .lineTo(595, 0)
                    .moveTo(0, -842)
                    .lineTo(0, 842)
                    .stroke();

            PdfPage curPage = srcDoc.getPage(1);
            PdfFormXObject xObject = curPage.copyAsFormXObject(pdfDoc);
            canvas.saveState()
                    .addXObjectAt(xObject, 0, 0)
                    .concatMatrix(0.5f, 0, 0, 0.5f, -595, 0)
                    .addXObjectAt(xObject, 0, 0)
                    .concatMatrix(1, 0, 0, 1, 595, 595)
                    .addXObjectAt(xObject, 0, 0)
                    .restoreState();

            canvas.saveState()
                    .concatMatrix(1, 0, 0.4f, 1, -750, -650)
                    .addXObjectAt(xObject, 0, 0)
                    .restoreState();

            canvas.saveState()
                    .concatMatrix(0, -1, -1, 0, 650, 0)
                    .addXObjectAt(xObject, 0, 0)
                    .concatMatrix(0.2f, 0, 0, 0.5f, 0, 300)
                    .addXObjectAt(xObject, 0, 0)
                    .restoreState();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
