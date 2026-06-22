package study.ywork.doc.itext.event;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PressPreviews {
    private static final String DEST = "pressPreviews.pdf";

    public static void main(String[] args) {
        new PressPreviews().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            doc.add(getTable());
        } catch (SQLException | FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public Table getTable() throws SQLException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 5, 1})).useAllAvailableWidth();
        table.setBorder(new SolidBorder(1));
        Style defaultCellStyle = new Style().setBorder(Border.NO_BORDER).
                setPaddingBottom(5).setPaddingTop(5).
                setPaddingLeft(5).setPaddingRight(5);

        for (int i = 0; i < 2; i++) {
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell().add(new Paragraph("Location")));
            cells.add(new Cell().add(new Paragraph("Date/Time")));
            cells.add(new Cell().add(new Paragraph("Run Length")));
            cells.add(new Cell().add(new Paragraph("Title")));
            cells.add(new Cell().add(new Paragraph("Year")));

            for (Cell c : cells) {
                c.addStyle(defaultCellStyle);
                c.setNextRenderer(new PressPreviewsCellRenderer(c));
                if (i == 0) {
                    table.addHeaderCell(c);
                } else {
                    table.addFooterCell(c);
                }
            }
        }

        List<Screening> screenings = SqlUtils.getPressPreviews();
        for (Screening screening : screenings) {
            Movie movie = screening.getMovie();
            Cell cell = new Cell().add(new Paragraph(screening.getLocation()))
                    .addStyle(defaultCellStyle);
            cell.setNextRenderer(new PressPreviewsCellRenderer(cell));
            table.addCell(cell);
            cell = new Cell().add(new Paragraph(String.format("%s   %2$tH:%2$tM", screening.getDate().toString(), screening.getTime())))
                    .addStyle(defaultCellStyle);
            cell.setNextRenderer(new PressPreviewsCellRenderer(cell));
            table.addCell(cell);
            cell = new Cell().add(new Paragraph(String.format("%d '", movie.getDuration())))
                    .addStyle(defaultCellStyle);
            cell.setNextRenderer(new PressPreviewsCellRenderer(cell));
            table.addCell(cell);
            cell = new Cell().add(new Paragraph(movie.getMovieTitle()))
                    .addStyle(defaultCellStyle);
            cell.setNextRenderer(new PressPreviewsCellRenderer(cell));
            table.addCell(cell);
            cell = new Cell().add(new Paragraph(String.valueOf(movie.getYear())))
                    .addStyle(defaultCellStyle);
            cell.setNextRenderer(new PressPreviewsCellRenderer(cell));
            table.addCell(cell);
        }

        return table;
    }


    private class PressPreviewsCellRenderer extends CellRenderer {
        public PressPreviewsCellRenderer(Cell modelElement) {
            super(modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            Rectangle rect = getOccupiedAreaBBox();
            drawContext.getCanvas()
                    .rectangle(rect.getLeft() + 2, rect.getBottom() + 2, rect.getWidth() - 4, rect.getHeight() - 4)
                    .stroke();
        }

        @Override
        public IRenderer getNextRenderer() {
            return new PressPreviewsCellRenderer((Cell) modelElement);
        }
    }
}
