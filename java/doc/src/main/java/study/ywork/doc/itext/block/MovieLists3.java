package study.ywork.doc.itext.block;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.properties.ListNumberingType;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MovieLists3 {
    private static final String DEST = "movieLists3.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        new MovieLists3().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        List list = new List(ListNumberingType.ROMAN_UPPER);
        DatabaseConnection connection = DBUtils.getFilmDBConnection();

        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(
                "SELECT DISTINCT mc.country_id, c.country, count(*) AS c "
                    + "FROM film_country c, film_movie_country mc "
                    + "WHERE c.id = mc.country_id "
                    + "GROUP BY mc.country_id, country ORDER BY c DESC");
            while (rs.next()) {
                ListItem item = new ListItem(String.format("%s: %d movies", rs.getString("country"), rs.getInt("c")));
                List movielist = new List(ListNumberingType.GREEK_LOWER);

                for (Movie movie : SqlUtils.getMovies(connection, rs.getString("country_id"))) {
                    ListItem movieitem = new ListItem(movie.getMovieTitle());
                    List directorlist = new List(ListNumberingType.ZAPF_DINGBATS_1);

                    for (Director director : movie.getDirectors()) {
                        directorlist.add(String.format("%s, %s", director.getName(), director.getGivenName()));
                    }

                    movieitem.add(directorlist);
                    movielist.add(movieitem);
                }

                item.add(movielist);
                list.add(item);
            }
        }

        connection.close();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            doc.add(list);
        }
    }
}
