package study.ywork.doc.itext.table;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import study.ywork.doc.domain.Country;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MovieTextMode {
    private static final String DEST = "movieTextMode.pdf";
    private final PdfFont normal;
    private final PdfFont bold;
    private final PdfFont italic;

    public MovieTextMode() throws IOException {
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
    }

    public static void main(String[] args) throws IOException {
        new MovieTextMode().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph("Movies:"));
            List<Movie> movies = SqlUtils.getMovies();

            for (Movie movie : movies) {
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4}))
                    .useAllAvailableWidth();
                Cell cell = new Cell(1, 2).add(new Paragraph(movie.getTitle()).setFont(bold));
                cell.setTextAlignment(TextAlignment.CENTER);
                table.addCell(cell);

                if (movie.getOriginalTitle() != null) {
                    Paragraph p = new Paragraph();
                    for (Text text : ElementFactory.getOriginalTitlePhrase(movie, italic, normal)) {
                        p.add(text);
                    }

                    cell = new Cell(1, 2).add(p);
                    cell.setTextAlignment(TextAlignment.RIGHT);
                    table.addCell(cell);
                }

                List<Director> directors = movie.getDirectors();
                cell = new Cell(directors.size(), 1).add(new Paragraph("Directors:"));
                cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                table.addCell(cell);
                int count = 0;

                for (Director pojo : directors) {
                    Paragraph p = new Paragraph();
                    for (Text text : ElementFactory.getDirectorPhrase(pojo, bold, normal)) {
                        p.add(text);
                    }

                    cell = new Cell().add(p);
                    cell.setMarginLeft(10F * count++);
                    table.addCell(cell);
                }

                table.addCell(new Cell().add(new Paragraph("Year:"))
                    .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(movie.getYear())))
                    .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph("Run length:"))
                    .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(movie.getDuration())))
                    .setTextAlignment(TextAlignment.RIGHT));
                List<Country> countries = movie.getCountries();
                cell = new Cell(countries.size(), 1).add(new Paragraph("Countries:"));
                cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
                table.addCell(cell);

                for (Country country : countries) {
                    table.addCell(new Cell().add(new Paragraph(country.getName()))
                        .setTextAlignment(TextAlignment.CENTER));
                }

                doc.add(table);
            }
        } catch (SQLException | FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
