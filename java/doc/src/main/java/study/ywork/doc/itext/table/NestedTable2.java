package study.ywork.doc.itext.table;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NestedTable2 {
    private static final String DEST = "nestedTable2.pdf";
    private static final String RESOURCE = "./src/main/resources/img/posters/%s.jpg";

    private final Map<String, ImageData> images;

    private final PdfFont bold;

    public NestedTable2() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.images = new HashMap<>();
    }

    public static void main(String[] args) throws IOException, SQLException {
        new NestedTable2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            List<Date> days = SqlUtils.getDays();
            int d = 1;

            for (Date day : days) {
                if (1 != d) {
                    doc.add(new AreaBreak());
                }
                
                doc.add(getTable(day));
                d++;
            }
        }
    }

    public Table getTable(Date day)
        throws SQLException, MalformedURLException {
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Cell cell = new Cell().add(new Paragraph(day.toString()).setFontColor(ColorConstants.WHITE));
        cell.setBackgroundColor(ColorConstants.BLACK);
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
        List<Screening> screenings = SqlUtils.getScreenings(day);

        for (Screening screening : screenings) {
            table.addCell(new Cell().add(getTable(screening)));
        }

        return table;
    }

    private Table getTable(Screening screening) throws MalformedURLException {
        // Create a table with 4 columns
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 5, 10, 10})).useAllAvailableWidth();
        // Get the movie
        Movie movie = screening.getMovie();
        // A cell with the title as a nested table spanning the complete row
        Cell cell = new Cell(1, 4);
        // nesting is done with addElement() in this example
        cell.add(fullTitle(screening));
        cell.setBorder(Border.NO_BORDER);

        DeviceRgb color = WebColors.getRGBColor(
            "#" + screening.getMovie().getEntry().getCategory().getColor());
        cell.setBackgroundColor(color);
        table.addCell(cell);
        // empty cell
        cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        // cell with the movie poster
        cell = new Cell().add(getImage(movie.getImdb()));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        // cell with the list of directors
        cell = new Cell();
        cell.add(ElementFactory.getDirectorList(movie));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        // cell with the list of countries
        cell = new Cell();
        cell.add(ElementFactory.getCountryList(movie));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        return table;
    }

    private Table fullTitle(Screening screening) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 15, 2})).useAllAvailableWidth();
        // cell 1: location and time
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.setBackgroundColor(ColorConstants.WHITE);
        String s = String.format("%s \u2013 %2$tH:%2$tM",
            screening.getLocation(), screening.getTime().getTime());
        cell.add(new Paragraph(s).setTextAlignment(TextAlignment.CENTER));
        table.addCell(cell);
        // cell 2: English and original title
        Movie movie = screening.getMovie();
        Paragraph p = new Paragraph();
        p.add(new Text(movie.getMovieTitle()).setFont(bold));
        p.setFixedLeading(16);
        if (movie.getOriginalTitle() != null) {
            p.add(new Text(" (" + movie.getOriginalTitle() + ")"));
        }
        cell = new Cell();
        cell.add(p);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        // cell 3 duration
        cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.setBackgroundColor(ColorConstants.WHITE);
        p = new Paragraph(String.format("%d'", movie.getDuration()));
        p.setTextAlignment(TextAlignment.CENTER);
        cell.add(p);
        table.addCell(cell);
        return table;
    }

    public Image getImage(String imdb) throws MalformedURLException {
        ImageData img = images.get(imdb);
        if (img == null) {
            img = ImageDataFactory.create(String.format(RESOURCE, imdb));
            images.put(imdb, img);
        }
        return new Image(img).scaleToFit(80, 72);
    }
}
