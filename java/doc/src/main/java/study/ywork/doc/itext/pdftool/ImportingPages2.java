package study.ywork.doc.itext.pdftool;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.IOException;

public class ImportingPages2 {
    private static final String DEST = "importingPages2.pdf";
    private static final String MOVIE_TEMPLATES = "movieTemplates.pdf";

    public static void main(String[] args) throws IOException {
        new ImportingPages2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        try (PdfDocument resultDoc = new PdfDocument(new PdfWriter(dest));
             PdfDocument srcDoc = new PdfDocument(new PdfReader(MOVIE_TEMPLATES));
             Document doc = new Document(resultDoc)) {
            for (int i = 1; i <= srcDoc.getNumberOfPages(); i++) {
                PdfPage curPage = srcDoc.getPage(i);
                PdfFormXObject header = curPage.copyAsFormXObject(resultDoc);
                Cell cell = new Cell()
                        .add(new Image(header).setWidth(UnitValue.createPercentValue(100)).setAutoScaleWidth(true))
                        .setBorder(new SolidBorder(1));
                cell.setNextRenderer(new MyCellRenderer(cell));
                table.addCell(cell);
            }

            doc.add(table);
        }
    }

    class MyCellRenderer extends CellRenderer {
        public MyCellRenderer(Cell modelElement) {
            super(modelElement);
        }

        @Override
        public IRenderer getNextRenderer() {
            return new MyCellRenderer((Cell) modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            Rectangle rect = getOccupiedAreaBBox();
            System.out.println(rect.getX());
            System.out.println(rect.getY());
            System.out.println(rect.getWidth());
            System.out.println(rect.getHeight());
            System.out.println();
        }
    }
}
