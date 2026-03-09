package study.ywork.doc.itext.position;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import study.ywork.doc.domain.Movie;

import java.io.IOException;

public class MovieColumns2 extends MovieColumns1 {
    private static final String DEST = "movieColumns2.pdf";

    public MovieColumns2() throws IOException {
        super();
    }

    public static void main(String[] args) throws Exception {
        new MovieColumns2().manipulatePdf(DEST);
    }

    @Override
    protected Paragraph createMovieInformation(Movie movie) {
        return super.createMovieInformation(movie)
                .setKeepTogether(true)
                .setPaddingLeft(0)
                .setFirstLineIndent(0)
                .setTextAlignment(TextAlignment.LEFT);
    }
}
