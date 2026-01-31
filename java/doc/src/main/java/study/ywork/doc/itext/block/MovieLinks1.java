package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MovieLinks1 {
    private static final String DEST = "movieLinks1.pdf";
    private final PdfFont bold;

    public MovieLinks1() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new MovieLinks1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String destination) throws IOException, SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        try (Statement stm = connection.createStatement();
             PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destination));
             Document doc = new Document(pdfDoc)) {
            ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT mc.country_id, c.country, count(*) AS c "
                            + "FROM film_country c, film_movie_country mc "
                            + "WHERE c.id = mc.country_id "
                            + "GROUP BY mc.country_id, country ORDER BY c DESC");
            while (rs.next()) {
                Paragraph anchor = new Paragraph(rs.getString("country"));
                anchor.setFont(bold);
                anchor.setDestination(rs.getString("country_id"));
                doc.add(anchor);

                for (Movie movie : SqlUtils.getMovies(connection, rs.getString("country_id"))) {
                    Link imdb = new Link(movie.getMovieTitle(),
                            PdfAction.createURI(String.format("https://www.imdb.com/title/tt%s/", movie.getImdb())));
                    doc.add(new Paragraph().add(imdb));
                }

                doc.add(new AreaBreak());
            }

            Link toUS = new Link("Go back to the first page.", PdfAction.createGoTo("US"));
            doc.add(new Paragraph(toUS));
        }

        connection.close();
    }
}
