package study.ywork.doc.itext.pdftool;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;

import java.io.IOException;

public class TilingHero {
    private static final String DEST = "tilingHero.pdf";
    private static final String SOURCE = "src/main/resources/pdfs/hero.pdf";

    public static void main(String[] rgs) {
        new TilingHero().manipulatePdf();
    }

    public void manipulatePdf() {
        try (PdfReader reader = new PdfReader(SOURCE);
             PdfDocument srcDoc = new PdfDocument(reader)) {
            writePdf(srcDoc);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void writePdf(PdfDocument srcDoc) throws IOException {
        Rectangle pageSize = srcDoc.getFirstPage().getPageSizeWithRotation();
        try (PdfWriter writer = new PdfWriter(DEST);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc, new PageSize(pageSize))) {
            pdfDoc.addNewPage();
            PdfFormXObject page = srcDoc.getFirstPage().copyAsFormXObject(pdfDoc);

            for (int i = 0; i < 16; i++) {
                float x = -pageSize.getWidth() * (i % 4);
                float y = pageSize.getHeight() * (i / 4 - 3);
                new PdfCanvas(pdfDoc.getLastPage())
                    .addXObjectWithTransformationMatrix(page, 4, 0, 0, 4, x, y);

                if (15 != i) {
                    doc.add(new AreaBreak());
                }
            }
        }
    }
}
