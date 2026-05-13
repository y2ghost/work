package study.ywork.doc.itext.block;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.properties.TextAlignment;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.PipeSplitCharacter;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MovieChain {
    private static final String DEST = "movieChain.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        new MovieChain().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        List<Movie> kubrick = SqlUtils.getMovies(connection, 1);
        connection.close();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(240, 240))) {
            doc.setMargins(10, 10, 10, 10);
            StringBuilder buf1 = new StringBuilder();

            for (Movie movie : kubrick) {
                buf1.append(movie.getMovieTitle().replace(' ', '\u00a0'));
                buf1.append('|');
            }

            Text chunk1 = new Text(buf1.toString());
            Paragraph paragraph = new Paragraph("A:\u00a0");
            paragraph.add(chunk1);
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            doc.add(paragraph);

            chunk1.setSplitCharacters(new PipeSplitCharacter());
            paragraph = new Paragraph("B:\u00a0");
            paragraph.add(chunk1);
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            doc.add(paragraph);

            StringBuilder buf2 = new StringBuilder();
            for (Movie movie : kubrick) {
                buf2.append(movie.getMovieTitle());
                buf2.append('|');
            }

            Text chunk2 = new Text(buf2.toString());
            paragraph = new Paragraph("C:\u00a0");
            paragraph.add(chunk2);
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            doc.add(paragraph);

            doc.add(new AreaBreak());
            chunk2.setHyphenation(new HyphenationConfig("en", "US", 2, 2));
            paragraph = new Paragraph("D:\u00a0");
            paragraph.add(chunk2);
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            doc.add(paragraph);

            doc.add(new AreaBreak());
            paragraph = new Paragraph("E:\u00a0");
            paragraph.add(chunk2);
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            paragraph.setSpacingRatio(1);
            doc.add(paragraph);
        }
    }
}
