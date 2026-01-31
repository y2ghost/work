package study.ywork.doc.itext.event;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
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

public class MovieCountries2 extends MovieCountries1 {
    private static final String DEST = "movieCountries2.pdf";

    public MovieCountries2() throws IOException {
        super();
    }

    public static void main(String[] args) throws IOException, SQLException {
        new MovieCountries2().manipulatePdf(DEST);
    }

    @Override
    public void manipulatePdf(String dest) throws IOException, SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
             Document doc = new Document(pdfDoc);
             Statement stm = connection.createStatement()) {
            doc.setMargins(54, 36, 36, 36);
            HeaderHandler headerHandler = new HeaderHandler();
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);
            WatermarkHandler watermarkHandler = new WatermarkHandler();
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, watermarkHandler);
            template = new PdfFormXObject(new Rectangle(550, 803, 30, 30));
            PdfCanvas canvas = new PdfCanvas(template, pdfDoc);
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

    public static class WatermarkHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            PdfFont font = null;

            try {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            } catch (IOException e) {
                e.printStackTrace();
            }

            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), docEvent.getDocument());
            try (Canvas lastCanvas = new Canvas(canvas, docEvent.getDocument().getLastPage().getPageSize())) {
                lastCanvas.setFontColor(new DeviceGray(0.75f))
                        .setFontSize(52)
                        .setFont(font)
                        .showTextAligned(new Paragraph("FOOBAR FILM FESTIVAL"), 297.5f, 421,
                                docEvent.getDocument().getNumberOfPages(), TextAlignment.CENTER,
                                VerticalAlignment.MIDDLE, 45);
            }
        }
    }
}
