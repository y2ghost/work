package study.ywork.doc.itext.block;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Text;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.MovieComparator;
import study.ywork.doc.util.SqlUtils;
import study.ywork.doc.util.StarSeparatorUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

public class DirectorOverview1 {
    private static final String DEST = "directorOverview1.pdf";
    private final PdfFont bold;
    private final PdfFont normal;
    private final PdfFont zapfDingbats;

    public DirectorOverview1() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        this.zapfDingbats = PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS, PdfEncodings.WINANSI);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new DirectorOverview1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        try (Statement stm = connection.createStatement();
             PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT d.id, d.name, d.given_name, count(*) AS c "
                            + "FROM film_director d, film_movie_director md "
                            + "WHERE d.id = md.director_id "
                            + "GROUP BY d.id, d.name, d.given_name ORDER BY name");
            while (rs.next()) {
                Director director = SqlUtils.getDirector(rs);
                Paragraph p = new Paragraph();

                for (Text text : ElementFactory.getDirectorPhrase(director, bold, normal)) {
                    p.add(text);
                }

                if (rs.getInt("c") > 2) {
                    Tab tab = new Tab();
                    tab.setNextRenderer(new PositionedArrowTabRenderer(tab, zapfDingbats, doc, true));
                    p.add(tab);
                }

                doc.add(new LineSeparator(new SolidLine(1)));
                doc.add(p);
                Set<Movie> movies = new TreeSet<>(
                        new MovieComparator(MovieComparator.BY_YEAR));
                movies.addAll(SqlUtils.getMovies(connection, rs.getInt("id")));

                for (Movie movie : movies) {
                    p = new Paragraph(movie.getMovieTitle());
                    p.add(": ");
                    p.add(new Text(String.valueOf(movie.getYear())));

                    if (movie.getYear() > 1999) {
                        Tab tab = new Tab();
                        tab.setNextRenderer(new PositionedArrowTabRenderer(tab, zapfDingbats, doc, false));
                        p.add(tab);
                    }

                    doc.add(p);
                }

                doc.add(StarSeparatorUtils.newStartDiv());
            }
        }

        connection.close();
    }
}
