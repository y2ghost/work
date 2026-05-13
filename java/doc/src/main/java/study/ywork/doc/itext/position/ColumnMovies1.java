package study.ywork.doc.itext.position;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

public class ColumnMovies1 {
    private static final String DEST = "columnMovies1.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";
    private static final Rectangle[] COLUMNS = {
            new Rectangle(36, 36, 188, 543),
            new Rectangle(230, 36, 188, 543),
            new Rectangle(424, 36, 188, 543),
            new Rectangle(618, 36, 188, 543),
    };
    private final PdfFont bold;
    private final PdfFont italic;
    private final PdfFont normal;

    public ColumnMovies1() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public static void main(String[] args) throws Exception {
        new ColumnMovies1().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, PageSize.A4.rotate())) {
            doc.setRenderer(new ColumnDocumentRenderer(doc, COLUMNS));
            List<Movie> movies = SqlUtils.getMovies();

            for (Movie movie : movies) {
                PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(String.format(RESOURCE, movie.getImdb())));
                Image img = new Image(imageXObject).scaleToFit(80, 1000);
                addContent(doc, movie, img);
            }
        } catch (MalformedURLException | FileNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addContent(Document doc, Movie movie, Image img) {
        Div div = new Div().
                setKeepTogether(true).
                setMarginBottom(15);
        div.add(img);
        div.add(new Paragraph(movie.getTitle()).setFont(bold).setMargins(0, 0, 0, 0));

        if (movie.getOriginalTitle() != null) {
            div.add(new Paragraph(movie.getOriginalTitle()).setFont(italic).setMargins(0, 0, 0, 0));
        }

        div.add(ElementFactory.getDirectorList(movie));
        div.add(new Paragraph().setMargins(0, 0, 0, 0).addAll(ElementFactory.getYearPhrase(movie, bold, normal)));
        div.add(new Paragraph().setMargins(0, 0, 0, 0).addAll(ElementFactory.getDurationPhrase(movie, bold, normal)));
        div.add(ElementFactory.getCountryList(movie));
        doc.add(div);
    }
}
