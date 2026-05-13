package study.ywork.doc.itext.event;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.TableRenderer;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class AlternatingBackground {
    private static final String DEST = "alternatingBackground.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        new AlternatingBackground().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            List<Date> days = SqlUtils.getDays();
            int d = 1;
            for (Date day : days) {
                if (1 != d) {
                    doc.add(new AreaBreak());
                }

                Table table = getTable(day);
                table.setNextRenderer(new AlternatingBackgroundTableRenderer(table, new Table.RowRange(0, table.getNumberOfRows() - 1)));
                doc.add(table);
                d++;
            }
        }
    }

    public Table getTable(Date day) throws SQLException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 2, 5, 1}));
        table.addHeaderCell(new Cell(1, 5)
                .add(new Paragraph(day.toString()))
                .setPadding(3)
                .setBackgroundColor(ColorConstants.RED)
                .setTextAlignment(TextAlignment.CENTER));

        table.addHeaderCell(new Cell()
                .add(new Paragraph("Location"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Time"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Run Length"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Title"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Year"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Location"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Time"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Run Length"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Title"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Year"))
                .setBackgroundColor(ColorConstants.ORANGE)
                .setTextAlignment(TextAlignment.LEFT));
        List<Screening> screenings = SqlUtils.getScreenings(day);

        for (Screening screening : screenings) {
            Movie movie = screening.getMovie();
            table.addCell(screening.getLocation());
            table.addCell(String.format("%1$tH:%1$tM", screening.getTime()));
            table.addCell(String.format("%d '", movie.getDuration()));
            table.addCell(movie.getMovieTitle());
            table.addCell(String.valueOf(movie.getYear()));
        }
        return table;
    }

    class AlternatingBackgroundTableRenderer extends TableRenderer {
        private boolean isOdd = true;

        public AlternatingBackgroundTableRenderer(Table modelElement, Table.RowRange rowRange) {
            super(modelElement, rowRange);
        }

        public AlternatingBackgroundTableRenderer(Table modelElement) {
            super(modelElement);
        }

        @Override
        public AlternatingBackgroundTableRenderer getNextRenderer() {
            return new AlternatingBackgroundTableRenderer((Table) modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            for (int i = 0; i < rows.size() && null != rows.get(i) && null != rows.get(i)[0]; i++) {
                CellRenderer[] renderers = rows.get(i);
                Rectangle rect = new Rectangle(renderers[0].getOccupiedArea().getBBox().getLeft(),
                        renderers[0].getOccupiedArea().getBBox().getBottom(),
                        renderers[renderers.length - 1].getOccupiedArea().getBBox().getRight() -
                                renderers[0].getOccupiedArea().getBBox().getLeft(),
                        renderers[0].getOccupiedArea().getBBox().getHeight());
                PdfCanvas canvas = drawContext.getCanvas();
                canvas.saveState();
                if (isOdd) {
                    canvas.setFillColor(ColorConstants.WHITE);
                    isOdd = false;
                } else {
                    canvas.setFillColor(ColorConstants.YELLOW);
                    isOdd = true;
                }
                canvas.rectangle(rect);
                canvas.fill();
                canvas.stroke();
                canvas.restoreState();
            }
            
            super.draw(drawContext);
        }
    }
}
