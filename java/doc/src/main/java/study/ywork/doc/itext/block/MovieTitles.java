package study.ywork.doc.itext.block;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MovieTitles {
    private static final String DEST = "movieTitles.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        new MovieTitles().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        List<Movie> movies = SqlUtils.getMovies();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            for (Movie movie : movies) {
                doc.add(new Paragraph(movie.getTitle()));
            }
        }
    }
}
