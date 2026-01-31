package study.ywork.doc.itext.position;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;
import study.ywork.doc.util.StarSeparatorUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ColumnMovies2 {
    private static final String DEST = "columnMovies2.pdf";
    private static final Rectangle[] COLUMNS = {
            new Rectangle(36, 36, 188, 543),
            new Rectangle(230, 36, 188, 543),
            new Rectangle(424, 36, 188, 543),
            new Rectangle(618, 36, 188, 543),
    };
    private final PdfFont bold;
    private final PdfFont italic;
    private final PdfFont normal;

    public ColumnMovies2() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public static void main(String[] args) throws Exception {
        new ColumnMovies2().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, PageSize.A4.rotate())) {
            doc.setRenderer(new ColumnDocumentRenderer(doc, COLUMNS));
            List<Movie> movies = SqlUtils.getMovies();
            for (Movie movie : movies) {
                addContent(doc, movie);
            }
        } catch (FileNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addContent(Document doc, Movie movie) {
        Div div = new Div().setKeepTogether(true);
        Paragraph p = new Paragraph(movie.getTitle()).setFont(bold).
                setTextAlignment(TextAlignment.CENTER).
                setMargins(0, 0, 0, 0);
        div.add(p);

        if (movie.getOriginalTitle() != null) {
            p = new Paragraph(movie.getOriginalTitle()).setFont(italic).
                    setTextAlignment(TextAlignment.RIGHT).
                    setMargins(0, 0, 0, 0);
            div.add(p);
        }

        p = new Paragraph().
                setMargins(0, 0, 0, 0).
                addAll(ElementFactory.getYearPhrase(movie, bold, normal)).
                add(" ").
                addAll(ElementFactory.getDurationPhrase(movie, bold, normal)).
                setTextAlignment(TextAlignment.JUSTIFIED_ALL);
        div.add(p);
        div.add(StarSeparatorUtils.newStartDiv());
        doc.add(div);
    }
}
