package study.ywork.doc.itext.position;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import study.ywork.doc.domain.Country;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MovieColumns1 {
    private static final String DEST = "movieColumns1.pdf";
    protected final PdfFont normal;
    protected final PdfFont bold;
    protected final PdfFont italic;
    protected final PdfFont boldItalic;

    public MovieColumns1() throws IOException {
        normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        boldItalic = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
    }

    public static void main(String[] args) throws Exception {
        new MovieColumns1().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) throws SQLException, FileNotFoundException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            doc.setProperty(Property.FONT, normal);
            Rectangle[] columns = {new Rectangle(36, 36, 260, 770), new Rectangle(299, 36, 260, 770)};
            doc.setRenderer(new ColumnDocumentRenderer(doc, columns));

            List<Movie> movies = SqlUtils.getMovies();
            for (Movie movie : movies) {
                doc.add(createMovieInformation(movie));
                doc.add(new LineSeparator(new SolidLine(0.3f)));
            }
        } catch (FileNotFoundException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected Paragraph createMovieInformation(Movie movie) {
        Paragraph p = new Paragraph().
                setTextAlignment(TextAlignment.JUSTIFIED).
                setPaddingLeft(27).
                setFirstLineIndent(-27).
                setMultipliedLeading(1.2f);

        p.add(new Text("Title: ").setFont(boldItalic));
        p.add(new Text(movie.getMovieTitle()).setFont(normal));
        p.add(" ");

        if (movie.getOriginalTitle() != null) {
            p.add(new Text("Original title: ").setFont(boldItalic));
            p.add(new Text(movie.getOriginalTitle() != null ? movie.getOriginalTitle() : "").setFont(italic));
            p.add(" ");
        }

        p.add(new Text("Country: ").setFont(boldItalic));
        for (Country country : movie.getCountries()) {
            p.add(country.getName());
            p.add(" ");
        }

        p.add(new Text("Director: ").setFont(boldItalic));
        for (Director director : movie.getDirectors()) {
            p.add(new Text(director.getName() + ", ").setFont(bold));
            p.add(new Text(director.getGivenName() + " "));
        }

        p.add(new Text("Year: ").setFont(boldItalic));
        p.add(new Text(String.valueOf(movie.getYear())));
        p.add(new Text(" Duration: ").setFont(boldItalic));
        p.add(new Text(String.valueOf(movie.getDuration())));
        p.add(new Text(" minutes"));
        return p;
    }
}
