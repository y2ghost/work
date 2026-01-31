package study.ywork.doc.itext.stream;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.action.PdfTarget;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

public class KubrickBox {
    private static final String DEST = "kubrickBox.pdf";
    private static final String IMG_BOX = "src/main/resources/images/kubrick_box.jpg";
    private static final String RESOURCE_FILES = "src/main/resources/pdfs/%s.pdf";
    private static final String RESOURCE_PDFS_PREFIX = "16_08_";

    public static void main(String[] args) {
        new KubrickBox().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Image img = new Image(ImageDataFactory.create(IMG_BOX));
            doc.add(img);
            List list = new List();
            list.setSymbolIndent(20);
            DatabaseConnection connection = DBUtils.getFilmDBConnection();
            Set<Movie> box = new TreeSet<>();
            box.addAll(SqlUtils.getMovies(connection, 1));
            box.addAll(SqlUtils.getMovies(connection, 4));
            connection.close();

            for (Movie movie : box) {
                if (movie.getYear() > 1960) {
                    pdfDoc.addFileAttachment(movie.getTitle(),
                        PdfFileSpec.createEmbeddedFileSpec(pdfDoc, String.format(RESOURCE_FILES, RESOURCE_PDFS_PREFIX + movie.getImdb()), null,
                            String.format("kubrick_%s.pdf", movie.getImdb()), null, null));
                    ListItem item = new ListItem(movie.getMovieTitle());
                    PdfTarget target = PdfTarget.createChildTarget(movie.getTitle());
                    Link link = new Link(" (see info)",
                        PdfAction.createGoToE(PdfExplicitRemoteGoToDestination.createFit(1), true, target));
                    item.add(new Paragraph(link));
                    list.add(item);
                }
            }
            doc.add(list);
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
