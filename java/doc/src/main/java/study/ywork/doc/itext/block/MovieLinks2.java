package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovieLinks2 {
    private static final String DEST = "movieLinks2.pdf";
    private static final String MOVIE_LINKS1 = "movieLinks1.pdf";
    private final PdfFont bold;
    private final PdfFont italic;

    public MovieLinks2() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new MovieLinks2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String destination) throws IOException, SQLException {
        List<Paragraph> paragraphs = new ArrayList<>();
        Paragraph p = new Paragraph();
        Text top = new Text("Country List").setFont(bold);
        top.setDestination("top");
        p.add(top);
        paragraphs.add(p);
        // 创建链接
        Link imdb = new Link("Internet Movie Database", PdfAction.createURI("https://www.imdb.com/"));
        imdb.setFont(italic);
        p = new Paragraph("Click on a country, and you'll get a list of movies, "
                + "containing links to the ");
        p.add(imdb);
        p.add(".");
        paragraphs.add(p);
        // 创建文件链接
        p = new Paragraph("This list can be found in a ");
        Link page1 = new Link("separate document", PdfAction.createGoToR(MOVIE_LINKS1, 1));
        p.add(page1);
        p.add(".");
        paragraphs.add(p);
        paragraphs.add(new Paragraph("\n"));

        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT mc.country_id, c.country, count(*) AS c "
                            + "FROM film_country c, film_movie_country mc "
                            + "WHERE c.id = mc.country_id "
                            + "GROUP BY mc.country_id, country ORDER BY c DESC");

            while (rs.next()) {
                Paragraph country = new Paragraph(rs.getString("country"));
                country.add(": ");
                Link link = new Link(String.format("%d movies", rs.getInt("c")),
                        PdfAction.createGoToR(MOVIE_LINKS1, rs.getString("country_id")));
                country.add(link);
                paragraphs.add(country);
            }

            paragraphs.add(new Paragraph("\n"));
            p = new Paragraph("Go to ");
            Link topLink = new Link("top", PdfAction.createGoTo("top"));
            p.add(topLink);
            p.add(".");
            paragraphs.add(p);
        }

        connection.close();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destination));
             Document doc = new Document(pdfDoc)) {
            paragraphs.forEach(doc::add);
        }
    }
}
