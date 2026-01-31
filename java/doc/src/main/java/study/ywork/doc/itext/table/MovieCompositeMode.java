package study.ywork.doc.itext.table;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;

public class MovieCompositeMode {
    private static final String DEST = "movieCompositeRole.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";
    private final PdfFont normal;
    private final PdfFont bold;
    private final PdfFont italic;

    public MovieCompositeMode() throws IOException {
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new MovieCompositeMode().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph("Movies:"));
            java.util.List<Movie> movies = SqlUtils.getMovies();

            for (Movie movie : movies) {
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 7}));
                table.setMarginTop(5);

                Cell cell = new Cell().add(new Image(ImageDataFactory.create(String.format(RESOURCE, movie.getImdb()))).setAutoScaleWidth(true));
                cell.setBorder(Border.NO_BORDER);
                table.addCell(cell);
                cell = new Cell();

                Paragraph p = new Paragraph(movie.getTitle()).setFont(bold);
                p.setTextAlignment(TextAlignment.CENTER);
                p.setMarginTop(5);
                p.setMarginBottom(5);
                cell.add(p);
                cell.setBorder(Border.NO_BORDER);

                if (movie.getOriginalTitle() != null) {
                    p = new Paragraph(movie.getOriginalTitle()).setFont(italic);
                    p.setTextAlignment(TextAlignment.RIGHT);
                    cell.add(p);
                }

                List list = ElementFactory.getDirectorList(movie);
                list.setMarginLeft(30);
                cell.add(list);
                p = new Paragraph(
                    String.format("Year: %d", movie.getYear())).setFont(normal);
                p.setMarginLeft(15);
                p.setFixedLeading(24);
                cell.add(p);
                p = new Paragraph(
                    String.format("Run length: %d", movie.getDuration())).setFont(normal);
                p.setFixedLeading(14);
                p.setMarginLeft(30);
                cell.add(p);
                list = ElementFactory.getCountryList(movie);
                list.setMarginLeft(40);
                cell.add(list);
                table.addCell(cell.setKeepTogether(true));
                doc.add(table);
            }
        }
    }
}
