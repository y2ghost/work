package study.ywork.doc.itext.block;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MoviePosters1 {
    private static final String DEST = "moviePosters1.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";

    public static void main(String[] args) throws IOException, SQLException {
        new MoviePosters1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Rectangle rect = new Rectangle(0, 806, 36, 842);
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.saveState()
                    .setStrokeColor(ColorConstants.RED)
                    .setFillColor(ColorConstants.RED)
                    .rectangle(rect)
                    .fillStroke()
                    .restoreState();
            List<Movie> movies = SqlUtils.getMovies();
            for (Movie movie : movies) {
                doc.add(new Paragraph(movie.getMovieTitle()));
                doc.add(new Image(ImageDataFactory.create(String.format(RESOURCE, movie.getImdb()))));
            }
        }
    }
}
