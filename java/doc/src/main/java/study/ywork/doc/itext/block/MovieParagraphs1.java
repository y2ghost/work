package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import study.ywork.doc.domain.Country;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieParagraphs1 {
    private static final String DEST = "movieParagraphs1.pdf";
    private PdfFont bold;
    private PdfFont boldItalic;
    private PdfFont italic;
    private PdfFont normal;

    public static void main(String[] args) throws IOException, SQLException {
        new MovieParagraphs1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        createFonts();
        List<Movie> movies = SqlUtils.getMovies();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            for (Movie movie : movies) {
                Paragraph p = createMovieInformation(movie);
                doc.add(p.setMarginLeft(18)
                        .setMarginTop(18)
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setFirstLineIndent(-18));
            }
        }
    }

    public Paragraph createMovieInformation(Movie movie) {
        Paragraph p = new Paragraph();
        p.setFont(normal);
        p.add(new Text("Title: ").setFont(boldItalic));
        for (Text text : ElementFactory.getMovieTitlePhrase(movie, normal)) {
            p.add(text);
        }
        p.add(" ");
        if (movie.getOriginalTitle() != null) {
            p.add(new Text("Original title: ").setFont(boldItalic));
            for (Text text : ElementFactory.getOriginalTitlePhrase(movie, italic, normal)) {
                p.add(text);
            }
            p.add(" ");
        }
        p.add(new Text("Country: ").setFont(boldItalic));
        for (Country country : movie.getCountries()) {
            for (Text text : ElementFactory.getCountryPhrase(country, normal)) {
                p.add(text);
            }
            p.add(" ");
        }
        p.add(new Text("Director: ").setFont(boldItalic));
        for (Director director : movie.getDirectors()) {
            for (Text text : ElementFactory.getDirectorPhrase(director, bold, normal)) {
                p.add(text);
            }
            p.add(" ");
        }
        for (Text text : createYearAndDuration(movie)) {
            p.add(text);
        }
        return p;
    }

    public List<Text> createYearAndDuration(Movie movie) {
        List<Text> info = new ArrayList<>();
        info.add(new Text("Year: ").setFont(boldItalic));
        info.add(new Text(String.valueOf(movie.getYear())).setFont(normal));
        info.add(new Text(" Duration: ").setFont(boldItalic));
        info.add(new Text(String.valueOf(movie.getDuration())).setFont(normal));
        info.add(new Text(" minutes").setFont(normal));
        return info;
    }

    protected void createFonts() throws IOException {
        bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        boldItalic = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
        italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }
}
