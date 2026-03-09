package study.ywork.doc.itext.position;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MovieColumns3 extends MovieColumns1 {
    private static final String DEST = "movieColumns3.pdf";

    private static final Rectangle[] COLUMNS = {
            new Rectangle(36, 666, 260, 136),
            new Rectangle(110, 580, 190, 90),
            new Rectangle(36, 480, 260, 100),
            new Rectangle(36, 390, 190, 90),
            new Rectangle(36, 36, 260, 350),
            new Rectangle(299, 480, 260, 322),
            new Rectangle(373, 390, 190, 90),
            new Rectangle(299, 250, 260, 136),
            new Rectangle(299, 150, 190, 100),
            new Rectangle(299, 36, 260, 110),
    };

    public MovieColumns3() throws IOException {
        super();
    }

    public static void main(String[] args) throws Exception {
        new MovieColumns3().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws SQLException, FileNotFoundException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            doc.setProperty(Property.FONT, normal);
            doc.setRenderer(new ColumnDocumentRenderer(doc, COLUMNS));
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new DrawPageEventHandler());
            List<Movie> movies = SqlUtils.getMovies();

            for (Movie movie : movies) {
                doc.add(createMovieInformation(movie));
            }
        }
    }

    public void drawRectangles(PdfCanvas canvas) {
        canvas.saveState();
        canvas.setFillColorGray(0.9f);
        canvas.rectangle(33, 592, 72, 72);
        canvas.rectangle(263, 406, 72, 72);
        canvas.rectangle(491, 168, 72, 72);
        canvas.fillStroke();
        canvas.restoreState();
    }

    @Override
    public Paragraph createMovieInformation(Movie movie) {
        return super.createMovieInformation(movie).
                setPaddingLeft(0).
                setFirstLineIndent(0);
    }

    private class DrawPageEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            drawRectangles(new PdfCanvas(((PdfDocumentEvent) event).getPage()));
        }
    }
}
