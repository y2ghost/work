package study.ywork.doc.itext.protect;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
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

public class HelloWorldCompression {
    private static final String[] RESULT = {
        "helloWorldCompression_compression_not_at_all.pdf",
        "helloWorldCompression_compression_zero.pdf",
        "helloWorldCompression_compression_normal.pdf",
        "helloWorldCompression_compression_high.pdf",
        "helloWorldCompression_compression_full.pdf",
        "helloWorldCompression_compression_full_too.pdf",
        "helloWorldCompression_compression_removed.pdf"
    };

    public static void main(String[] args) {
        new HelloWorldCompression().manipulatePdf();
    }

    private void createPdf(String dest, int compression) {
        WriterProperties properties = new WriterProperties();
        switch (compression) {
            case -1:
                properties.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
                break;
            case 0:
                properties.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                break;
            case 2:
                properties.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
                break;
            case 3:
                properties.setFullCompressionMode(true);
                break;
            default:
                // NOOP
        }

        try (PdfWriter writer = new PdfWriter(dest, properties);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {
            PdfFont boldItalic = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
            DatabaseConnection connection = DBUtils.getFilmDBConnection();

            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT mc.country_id, c.country, count(*) AS c "
                        + "FROM film_country c, film_movie_country mc "
                        + "WHERE c.id = mc.country_id "
                        + "GROUP BY mc.country_id, country ORDER BY c DESC");
                List list = new List(ListNumberingType.DECIMAL);

                while (rs.next()) {
                    ListItem item = new ListItem(
                        String.format("%s: %d movies",
                            rs.getString("country"), rs.getInt("c")));
                    item.setFont(boldItalic);
                    List movielist = new List(ListNumberingType.ENGLISH_LOWER);

                    for (Movie movie :
                        SqlUtils.getMovies(connection, rs.getString("country_id"))) {
                        ListItem movieitem = new ListItem(movie.getMovieTitle());
                        List directorlist = new List();
                        for (Director director : movie.getDirectors()) {
                            directorlist.add(
                                String.format("%s, %s",
                                    director.getName(), director.getGivenName()));
                        }
                        movieitem.add(directorlist);
                        movielist.add(movieitem);
                    }

                    item.add(movielist);
                    list.add(item);
                }

                doc.add(list);
            }

            connection.close();
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void compressPdf(String src, String dest) {
        try (PdfWriter writer = new PdfWriter(dest, new WriterProperties()
            .setPdfVersion(PdfVersion.PDF_1_5)
            .setFullCompressionMode(true));
             PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), writer)) {
            // NOOP
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void decompressPdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            // NOOP
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void manipulatePdf() {
        createPdf(RESULT[0], -1);
        createPdf(RESULT[1], 0);
        createPdf(RESULT[2], 1);
        createPdf(RESULT[3], 2);
        createPdf(RESULT[4], 3);
        compressPdf(RESULT[1], RESULT[5]);
        decompressPdf(RESULT[5], RESULT[6]);
    }
}
