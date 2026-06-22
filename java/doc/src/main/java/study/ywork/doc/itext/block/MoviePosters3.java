package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MoviePosters3 {
    private static final String DEST = "moviePosters3.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";
    private final PdfFont bold;

    public MoviePosters3() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new MoviePosters3().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        List<Movie> movies = SqlUtils.getMovies();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            for (Movie movie : movies) {
                Image img = new Image(ImageDataFactory.create(String.format(RESOURCE, movie.getImdb())));
                img.setHorizontalAlignment(HorizontalAlignment.LEFT);
                img.setBorder(new SolidBorder(ColorConstants.WHITE, 10));
                img.scaleToFit(1000, 72);
                doc.add(img);

                doc.add(new Paragraph(movie.getMovieTitle()).setFont(bold).setFixedLeading(18));
                doc.add(ElementFactory.getCountryList(movie));
                doc.add(new Paragraph(String.format("Year: %d", movie.getYear())).setFixedLeading(18));
                doc.add(new Paragraph(String.format("Duration: %d minutes", movie.getDuration())).setFixedLeading(18));
                doc.add(new Paragraph("Directors:").setFixedLeading(18));
                doc.add(ElementFactory.getDirectorList(movie));
                doc.add(new Paragraph("\n").setFixedLeading(18));
            }
        }
    }
}
