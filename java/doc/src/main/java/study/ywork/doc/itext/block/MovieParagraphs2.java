package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
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

public class MovieParagraphs2 {
    private static final String DEST = "movieParagraphs2.pdf";
    private final PdfFont bold;
    private final PdfFont boldItalic;
    private final PdfFont italic;
    private final PdfFont normal;

    public static void main(String[] args) throws IOException, SQLException {
        new MovieParagraphs2().manipulatePdf(DEST);
    }

    public MovieParagraphs2() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.boldItalic = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
        this.italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        List<Movie> movies = SqlUtils.getMovies();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            for (Movie movie : movies) {
                List<Paragraph> paragraphs = buildMovieParagraphs(movie);
                paragraphs.forEach(doc::add);
            }
        }
    }

    private List<Text> createYearAndDuration(Movie movie) {
        List<Text> info = new ArrayList<>();
        info.add(new Text("Year: ").setFont(boldItalic));
        info.add(new Text(String.valueOf(movie.getYear())).setFont(normal));
        info.add(new Text(" Duration: ").setFont(boldItalic));
        info.add(new Text(String.valueOf(movie.getDuration())).setFont(normal));
        info.add(new Text(" minutes").setFont(normal));
        return info;
    }

    private List<Paragraph> buildMovieParagraphs(Movie movie) {
        List<Paragraph> paragraphs = new ArrayList<>();

        Paragraph title = buildTitle(movie);
        paragraphs.add(title);

        if (movie.getOriginalTitle() != null) {
            Paragraph dummy = new Paragraph("\u00a0").setFont(normal);
            dummy.setFixedLeading(-18);
            paragraphs.add(dummy);
            Paragraph originalTitle = new Paragraph();

            for (Text text : ElementFactory.getOriginalTitlePhrase(movie, italic, normal)) {
                originalTitle.add(text);
            }

            originalTitle.setTextAlignment(TextAlignment.RIGHT);
            paragraphs.add(originalTitle);
        }

        Paragraph director;
        float indent = 20;

        for (Director pojo : movie.getDirectors()) {
            director = new Paragraph();
            for (Text text : ElementFactory.getDirectorPhrase(pojo, bold, normal)) {
                director.add(text);
            }

            director.setMarginLeft(indent);
            paragraphs.add(director);
            indent += 20;
        }

        Paragraph country;
        indent = 20;

        for (Country pojo : movie.getCountries()) {
            country = new Paragraph();
            for (Text text : ElementFactory.getCountryPhrase(pojo, normal)) {
                country.add(text);
            }

            country.setTextAlignment(TextAlignment.RIGHT);
            country.setMarginRight(indent);
            paragraphs.add(country);
            indent += 20;
        }

        Paragraph info = new Paragraph();
        for (Text text : createYearAndDuration(movie)) {
            info.add(text);
        }

        info.setTextAlignment(TextAlignment.CENTER);
        info.setMarginTop(36);
        paragraphs.add(info);
        return paragraphs;
    }

    private Paragraph buildTitle(Movie movie) {
        Paragraph title = new Paragraph();
        for (Text text : ElementFactory.getMovieTitlePhrase(movie, bold)) {
            title.add(text);
        }

        title.setHorizontalAlignment(HorizontalAlignment.LEFT);
        return title;
    }
}
