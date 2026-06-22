package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TabAlignment;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.hsqldb.HsqldbConnection;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.MovieComparator;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DirectorOverview3 {
    private static final String DEST = "directorOverview3.pdf";
    private final PdfFont bold;
    private final PdfFont normal;

    public DirectorOverview3() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new DirectorOverview3().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        DatabaseConnection connection = new HsqldbConnection("filmfestival");
        try (Statement stm = connection.createStatement();
             PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT d.id, d.name, d.given_name, count(*) AS c "
                            + "FROM film_director d, film_movie_director md "
                            + "WHERE d.id = md.director_id "
                            + "GROUP BY d.id, d.name, d.given_name ORDER BY c DESC");
            List<TabStop> tabStops = new ArrayList<>();
            tabStops.add(new TabStop(200, TabAlignment.LEFT));
            tabStops.add(new TabStop(350, TabAlignment.LEFT));
            tabStops.add(new TabStop(450, TabAlignment.LEFT, new DottedLine()));

            while (rs.next()) {
                Div div = new Div();
                Director director = SqlUtils.getDirector(rs);
                Paragraph p = new Paragraph();

                for (Text text : ElementFactory.getDirectorPhrase(director, bold, normal)) {
                    p.add(text);
                }

                SolidLine line = new SolidLine(.5f);
                line.setColor(ColorConstants.BLUE);
                p.addTabStops(new TabStop(1000, TabAlignment.RIGHT, line));
                p.add(new Tab());
                p.add(String.format("movies: %d", rs.getInt("c")));
                div.add(p);
                div.add(new LineSeparator(new SolidLine(1)));
                doc.add(div);

                Set<Movie> movies = new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
                movies.addAll(SqlUtils.getMovies(connection, rs.getInt("id")));

                for (Movie movie : movies) {
                    p = new Paragraph();
                    p.addTabStops(tabStops);
                    p.add(movie.getMovieTitle());
                    p.add(new Tab());

                    if (movie.getOriginalTitle() != null) {
                        p.add(new Text(movie.getOriginalTitle()));
                    }

                    p.add(new Tab());
                    p.add(new Text(String.format("%d minutes", movie.getDuration())));
                    p.add(new Tab());
                    p.add(new Text(String.valueOf(movie.getYear())));
                    doc.add(p);
                }
            }
        }

        connection.close();
    }
}
