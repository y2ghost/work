package study.ywork.doc.itext.table;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ColumnTable {
    private static final String DEST = "columnTable.pdf";
    private final PdfFont bold;
    private Date date;

    public ColumnTable() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD, PdfEncodings.WINANSI);
    }

    public static void main(String[] args) throws IOException {
        new ColumnTable().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            ColumnDocumentRenderer renderer = new ColumnDocumentRenderer(doc);
            doc.setRenderer(renderer);
            List<Date> days = SqlUtils.getDays();
            date = days.get(0);

            for (int i = 0; i < days.size(); i++) {
                doc.add(getTable(days.get(i)));
                if (days.size() - 1 != i) {
                    date = days.get(i + 1);
                    int currentPageNumber = renderer.getCurrentArea().getPageNumber();

                    while (renderer.getCurrentArea().getPageNumber() == currentPageNumber) {
                        doc.add(new AreaBreak());
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static Table getHeaderTable(Date day, int page, PdfFont font) {
        Table header = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        Style style = new Style().setBackgroundColor(ColorConstants.BLACK).setFontColor(ColorConstants.WHITE).setFont(font);
        Paragraph p = new Paragraph("Foobar Film Festival").addStyle(style);
        header.addCell(new Cell().add(p));
        p = new Paragraph(day.toString()).addStyle(style);
        header.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
        p = new Paragraph(String.format("page %d", page)).addStyle(style);
        header.addCell(new Cell().add(p).setTextAlignment(TextAlignment.RIGHT));
        return header;
    }

    public Table getTable(Date day) throws SQLException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1.5f, 2, 4.5f, 1}));
        Style style = new Style().setBackgroundColor(ColorConstants.LIGHT_GRAY);
        table.addHeaderCell(new Cell().add(new Paragraph("Location")).addStyle(style));
        table.addHeaderCell(new Cell().add(new Paragraph("Time")).addStyle(style));
        table.addHeaderCell(new Cell().add(new Paragraph("Run Length")).addStyle(style));
        table.addHeaderCell(new Cell().add(new Paragraph("Title")).addStyle(style));
        table.addHeaderCell(new Cell().add(new Paragraph("Year")).addStyle(style));
        table.addFooterCell(new Cell().add(new Paragraph("Location")).addStyle(style));
        table.addFooterCell(new Cell().add(new Paragraph("Time")).addStyle(style));
        table.addFooterCell(new Cell().add(new Paragraph("Run Length")).addStyle(style));
        table.addFooterCell(new Cell().add(new Paragraph("Title")).addStyle(style));
        table.addFooterCell(new Cell().add(new Paragraph("Year")).addStyle(style));

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

    protected class ColumnDocumentRenderer extends DocumentRenderer {
        private int nextAreaNumber = 0;

        private int currentPageNumber = 0;

        public ColumnDocumentRenderer(Document document) {
            super(document);
        }

        @Override
        public IRenderer getNextRenderer() {
            return new ColumnDocumentRenderer(document);
        }

        @Override
        public LayoutArea updateCurrentArea(LayoutResult overflowResult) {
            if (nextAreaNumber % 2 == 0) {
                currentPageNumber = super.updateCurrentArea(overflowResult).getPageNumber();
                addChild(getHeaderTable(date, currentPageNumber, bold).createRendererSubTree());
                nextAreaNumber++;
                currentArea = new RootLayoutArea(currentPageNumber, new Rectangle(36, 36, 383, 450));
            } else {
                nextAreaNumber++;
                currentArea = new RootLayoutArea(currentPageNumber, new Rectangle(423, 36, 383, 450));
            }

            return currentArea;
        }
    }
}
