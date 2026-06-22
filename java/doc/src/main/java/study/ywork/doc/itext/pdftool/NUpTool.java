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

public class NUpTool {
    private static final String DEST = "nupTool.pdf";
    private static final String RESULT = "nupTool%dup.pdf";
    private static final String STATIONERY = "stationery.pdf";

    public static void main(String[] args) throws IOException {
        new NUpTool().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        NUpTool nUpTool = new NUpTool();
        nUpTool.multiplePdf(STATIONERY, RESULT, 1);
        nUpTool.multiplePdf(STATIONERY, RESULT, 2);
        nUpTool.multiplePdf(STATIONERY, RESULT, 3);
        nUpTool.multiplePdf(STATIONERY, RESULT, 4);
        concatenateResults(DEST, new String[]{String.format(RESULT, 2), String.format(RESULT, 4),
            String.format(RESULT, 8), String.format(RESULT, 16)});
    }

    public void multiplePdf(String source, String destination, int pow) throws IOException {
        try (PdfDocument srcDoc = new PdfDocument(new PdfReader(source))) {
            Rectangle pageSize = srcDoc.getDefaultPageSize();
            Rectangle newSize = (pow % 2) == 0 ?
                new Rectangle(pageSize.getWidth(), pageSize.getHeight()) :
                new Rectangle(pageSize.getHeight(), pageSize.getWidth());
            Rectangle unitSize = new Rectangle(pageSize.getWidth(), pageSize.getHeight());

            for (int i = 0; i < pow; i++) {
                unitSize = new Rectangle(unitSize.getHeight() / 2, unitSize.getWidth());
            }

            int n = (int) Math.pow(2, pow);
            int r = (int) Math.pow(2, (pow / 2));
            int c = n / r;

            try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(String.format(destination, n)));
                 Document doc = new Document(pdfDoc, new PageSize(newSize))) {
                pdfDoc.addNewPage();
                int total = srcDoc.getNumberOfPages();

                for (int i = 1; i <= total; i++) {
                    Rectangle currentSize = srcDoc.getPage(i).getPageSize();
                    float factor = Math.min(
                        unitSize.getWidth() / currentSize.getWidth(),
                        unitSize.getHeight() / currentSize.getHeight());
                    float offsetX = unitSize.getWidth() * ((i % n) % c)
                        + (unitSize.getWidth() - (currentSize.getWidth() * factor)) / 2f;
                    float offsetY = newSize.getHeight() - (unitSize.getHeight() * (((i % n) / c) + 1))
                        + (unitSize.getHeight() - (currentSize.getHeight() * factor)) / 2f;
                    PdfFormXObject page = srcDoc.getPage(i).copyAsFormXObject(pdfDoc);
                    new PdfCanvas(pdfDoc.getLastPage().newContentStreamBefore(), pdfDoc.getLastPage().getResources(), pdfDoc)
                        .addXObjectWithTransformationMatrix(page, factor, 0, 0, factor, offsetX, offsetY);
                    if (i % n == 0 && i != total) {
                        doc.add(new AreaBreak());
                    }
                }
            }
        }
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
