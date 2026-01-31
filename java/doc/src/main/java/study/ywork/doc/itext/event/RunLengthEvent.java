package study.ywork.doc.itext.event;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class RunLengthEvent {
    private static final String DEST = "runLengthEvent.pdf";

    public static void main(String[] args) {
        new RunLengthEvent().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            List<Date> days = SqlUtils.getDays();
            int d = 1;

            for (Date day : days) {
                if (1 != d) {
                    doc.add(new AreaBreak());
                }
                doc.add(getTable(day));
                d++;
            }
        } catch (SQLException | FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public Table getTable(Date day) throws SQLException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 2, 5, 1}))
                .setFixedLayout().useAllAvailableWidth();
        table.addHeaderCell(new Cell(1, 5)
                .add(new Paragraph(day.toString()))
                .setPadding(3)
                .setBackgroundColor(ColorConstants.RED)
                .setTextAlignment(TextAlignment.CENTER));
        Style style = new Style();
        style
                .setBackgroundColor(ColorConstants.YELLOW)
                .setTextAlignment(TextAlignment.LEFT)
                .setPaddingLeft(3)
                .setPaddingRight(3)
                .setPaddingTop(3)
                .setPaddingBottom(3);
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Location"))
                .addStyle(style));
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Time"))
                .addStyle(style));
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Run Length"))
                .addStyle(style));
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Title"))
                .addStyle(style));
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Year"))
                .addStyle(style));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Location"))
                .addStyle(style));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Time"))
                .addStyle(style));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Run Length"))
                .addStyle(style));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Title"))
                .addStyle(style));
        table.addFooterCell(new Cell()
                .add(new Paragraph("Year"))
                .addStyle(style));
        List<Screening> screenings = SqlUtils.getScreenings(day);

        for (Screening screening : screenings) {
            Movie movie = screening.getMovie();
            table.addCell(screening.getLocation());
            table.addCell(String.format("%1$tH:%1$tM", screening.getTime()));
            Cell runLength = new Cell();
            runLength.setNextRenderer(new FilmCellRenderer(runLength, movie.getDuration(), false));
            runLength.add(new Paragraph(String.format("%d '", movie.getDuration())));

            if (screening.isPress()) {
                runLength.setNextRenderer(new FilmCellRenderer(runLength, movie.getDuration(), true));
            }

            table.addCell(runLength);
            table.addCell(movie.getMovieTitle());
            table.addCell(String.valueOf(movie.getYear()));
        }

        return table;
    }


    private class FilmCellRenderer extends CellRenderer {
        private int duration;
        private boolean isPressPreview;

        public FilmCellRenderer(Cell modelElement, int duration, boolean isPressPreview) {
            super(modelElement);
            this.duration = duration;
            this.isPressPreview = isPressPreview;
        }

        @Override
        public void drawBackground(DrawContext drawContext) {
            PdfCanvas canvas = drawContext.getCanvas();
            canvas.saveState();

            if (duration < 90) {
                canvas.setFillColor(new DeviceRgb(0x7C, 0xFC, 0x00));
            } else if (duration > 120) {
                canvas.setFillColor(new DeviceRgb(0x8B, 0x00, 0x00));
            } else {
                canvas.setFillColor(new DeviceRgb(0xFF, 0xA5, 0x00));
            }

            Rectangle rect = getOccupiedAreaBBox();
            canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth() * duration / 240, rect.getHeight());
            canvas.fill();
            canvas.restoreState();
            super.drawBackground(drawContext);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            if (isPressPreview) {
                PdfCanvas canvas = drawContext.getCanvas();
                canvas.beginText();
                try {
                    canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Rectangle rect = getOccupiedAreaBBox();
                canvas.moveText(rect.getLeft() + rect.getWidth() / 4, rect.getBottom() + 4.5f);
                canvas.showText("PRESS PREVIEW");
                canvas.endText();
            }
        }

        @Override
        public IRenderer getNextRenderer() {
            return new FilmCellRenderer((Cell) getModelElement(), duration, isPressPreview);
        }
    }
}
