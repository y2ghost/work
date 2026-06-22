package study.ywork.doc.itext.table;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class HeaderFooter {
    private static final String DEST = "headerFooter.pdf";

    public static void main(String[] args) {
        new HeaderFooter().manipulatePdf(DEST);
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
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 2, 5, 1, 3, 2}));
        Cell cell = new Cell(1, 7).add(new Paragraph(day.toString()).setFontColor(ColorConstants.WHITE));
        cell.setBackgroundColor(ColorConstants.BLACK);
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);

        table.addHeaderCell(new Cell().add(new Paragraph("Location")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Time")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Run Length")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Title")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Year")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Directors")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Countries")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        table.addFooterCell(new Cell().add(new Paragraph("Location")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addFooterCell(new Cell().add(new Paragraph("Time")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addFooterCell(new Cell().add(new Paragraph("Run Length")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addFooterCell(new Cell().add(new Paragraph("Title")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addFooterCell(new Cell().add(new Paragraph("Year")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addFooterCell(new Cell().add(new Paragraph("Directors")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addFooterCell(new Cell().add(new Paragraph("Countries")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        List<Screening> screenings = SqlUtils.getScreenings(day);
        for (Screening screening : screenings) {
            Movie movie = screening.getMovie();
            table.addCell(screening.getLocation());
            table.addCell(String.format("%1$tH:%1$tM", screening.getTime()));
            table.addCell(String.format("%d '", movie.getDuration()));
            table.addCell(movie.getMovieTitle());
            table.addCell(String.valueOf(movie.getYear()));
            cell = new Cell();
            cell.add(ElementFactory.getDirectorList(movie));
            table.addCell(cell);
            cell = new Cell();
            cell.add(ElementFactory.getCountryList(movie));
            table.addCell(cell);
        }

        return table;
    }
}
