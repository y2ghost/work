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

public class TransformationMatrix2 {
    private static final String DEST = "transformationMatrix2.pdf";
    private static final String RESOURCE = "src/main/resources/pdfs/logo.pdf";

    public static void main(String[] args) {
        new TransformationMatrix2().manipulatePdf(DEST);
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
            canvas.addXObjectAt(xObject, 0, 0)
                    .addXObjectWithTransformationMatrix(xObject, 0.5f, 0, 0, 0.5f, -595, 0)
                    .addXObjectWithTransformationMatrix(xObject, 0.5f, 0, 0, 0.5f, -297.5f, 297.5f)
                    .addXObjectWithTransformationMatrix(xObject, 1, 0, 0.4f, 1, -750, -650)
                    .addXObjectWithTransformationMatrix(xObject, 0, -1, -1, 0, 650, 0)
                    .addXObjectWithTransformationMatrix(xObject, 0, -0.2f, -0.5f, 0, 350, 0);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
