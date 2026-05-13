package study.ywork.doc.itext.event;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.MovieComparator;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

public class MovieCountries1 {
    private static final String DEST = "movieCountries1.pdf";
    protected PdfFont bold;
    protected PdfFont italic;
    protected PdfFont normal;
    protected PdfFormXObject template;

    public MovieCountries1() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new MovieCountries1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4), true);
             Statement stm = connection.createStatement()) {
            doc.setMargins(54, 36, 36, 36);
            template = new PdfFormXObject(new Rectangle(550, 803, 30, 30));
            PdfCanvas canvas = new PdfCanvas(template, pdfDoc);
            HeaderHandler headerHandler = new HeaderHandler();
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);
            ResultSet rs = stm.executeQuery("SELECT country, id FROM film_country ORDER BY country");
            int d = 1;

            while (rs.next()) {
                headerHandler.setHeader(rs.getString("country"));
                if (1 != d) {
                    doc.add(new AreaBreak());
                }

                Set<Movie> movies = new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
                movies.addAll(SqlUtils.getMovies(connection, rs.getString("id")));

                for (Movie movie : movies) {
                    doc.add(new Paragraph(movie.getMovieTitle()).setFont(bold));
                    if (movie.getOriginalTitle() != null)
                        doc.add(new Paragraph(movie.getOriginalTitle()).setFont(italic));
                    doc.add(new Paragraph(String.format("Year: %d; run length: %d minutes",
                            movie.getYear(), movie.getDuration())).setFont(normal));
                    doc.add(ElementFactory.getDirectorList(movie));
                }

                d++;
            }

            canvas.beginText();
            try {
                canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12);
            } catch (IOException e) {
                e.printStackTrace();
            }

            canvas.moveText(550, 803);
            canvas.showText(Integer.toString(pdfDoc.getNumberOfPages()));
            canvas.endText();
            canvas.stroke();
        }

        connection.close();
    }

    public class HeaderHandler implements IEventHandler {
        private String country;

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            int pageNum = docEvent.getDocument().getPageNumber(page);
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.beginText();

            try {
                canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            canvas.moveText(34, 803);
            canvas.showText(country);
            canvas.moveText(450, 0);
            canvas.showText(String.format("Page %d of", pageNum));
            canvas.endText();
            canvas.stroke();
            canvas.addXObject(template);
            canvas.release();
        }

        public void setHeader(String country) {
            this.country = country;
        }
    }
}
