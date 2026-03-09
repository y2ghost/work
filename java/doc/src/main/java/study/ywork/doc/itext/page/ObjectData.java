package study.ywork.doc.itext.page;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfUserPropertiesAttributes;
import com.itextpdf.kernel.pdf.tagging.PdfUserProperty;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.element.Image;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ObjectData {
    private static final String DEST = "objectData.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";
    private static final String SELECTDIRECTORS
            = "SELECT DISTINCT d.id, d.name, d.given_name, count(*) AS c "
            + "FROM film_director d, film_movie_director md "
            + "WHERE d.id = md.director_id AND d.id < 8 "
            + "GROUP BY d.id, d.name, d.given_name ORDER BY id";

    public static void main(String[] args) {
        new ObjectData().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            pdfDoc.setTagged();
            pdfDoc.setUserProperties(true);
            pdfDoc.getStructTreeRoot().getRoleMap().put(new PdfName("Directors"), PdfName.H);

            for (int i = 1; i < 8; i++) {
                pdfDoc.getStructTreeRoot().getRoleMap().put(new PdfName("director" + i), PdfName.P);
            }

            DatabaseConnection connection = DBUtils.getFilmDBConnection();
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            tagPointer.setPageForTagging(pdfDoc.addNewPage());
            tagPointer.addTag("Directors");

            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery(SELECTDIRECTORS);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Director director = SqlUtils.getDirector(rs);
                    PdfUserPropertiesAttributes userProperties = new PdfUserPropertiesAttributes();
                    userProperties.addUserProperty(new PdfUserProperty("Name", director.getName()));
                    userProperties.addUserProperty(new PdfUserProperty("Given name", director.getGivenName()));
                    userProperties.addUserProperty(new PdfUserProperty("Posters", rs.getInt("c")));
                    tagPointer.addTag("director" + id).getProperties().addAttributes(userProperties);
                    tagPointer.moveToParent();
                }
            }

            Map<Movie, Integer> map = new TreeMap<>();
            for (int i = 1; i < 8; i++) {
                List<Movie> movies = SqlUtils.getMovies(connection, i);
                for (Movie movie : movies) {
                    map.put(movie, i);
                }
            }

            PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
            float x = 11.5f;
            float y = 769.7f;

            for (Map.Entry<Movie, Integer> entry : map.entrySet()) {
                ImageData img = ImageDataFactory.create(String.format(RESOURCE, entry.getKey().getImdb()));
                Image image = new Image(img);
                tagPointer.moveToKid(entry.getValue() - 1);
                tagPointer.addTag(image.getAccessibilityProperties().getRole());
                canvas.openTag(tagPointer.getTagReference());
                canvas.addImageFittedIntoRectangle(img, new Rectangle(x + (45 - 30) / 2F, y, 30, 46), false);
                canvas.closeTag();
                tagPointer.moveToParent();
                tagPointer.moveToParent();

                x += 48;
                if (x > 578) {
                    x = 11.5f;
                    y -= 84.2f;
                }
            }
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
