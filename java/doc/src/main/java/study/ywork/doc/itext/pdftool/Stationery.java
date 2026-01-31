package study.ywork.doc.itext.pdftool;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import study.ywork.doc.domain.CountryInfo;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Stationery {
    private static final String DEST = "stationery.pdf";
    private static final String SOURCE = "stationery_watermark.pdf";
    private final PdfFont bold;
    private final PdfFont italic;
    private final PdfFont normal;

    public Stationery() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        this.normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new Stationery().manipulatePdf(DEST);
    }
    
    public void manipulatePdf(String dest) throws SQLException, IOException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        createStationery(SOURCE);

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4));
             Statement stm = connection.createStatement()) {
            StationeryEventHandler eventHandler = new StationeryEventHandler();
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);
            doc.setMargins(72, 36, 36, 36);

            ResultSet rs = stm.executeQuery("SELECT country, id FROM film_country ORDER BY country");
            java.util.List<CountryInfo> countryInfos = new ArrayList<>();

            while (rs.next()) {
                CountryInfo info = new CountryInfo();
                info.setId(rs.getString("id"));
                info.setName(rs.getString("country"));
                countryInfos.add(info);
            }

            Iterator<CountryInfo> infoIterator = countryInfos.iterator();
            while (infoIterator.hasNext()) {
                addCountryInfo(connection, infoIterator.next(), doc);
                if (infoIterator.hasNext()) {
                    doc.add(new AreaBreak());
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        connection.close();
    }

    public void createStationery(String filename) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
             Document doc = new Document(pdfDoc)) {
            // 字体不能复用，每个PDF只能单独生成示例
            PdfFont localFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont localBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Table table = new Table(UnitValue.createPercentArray(1))
                .useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(80))
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
            Style style = new Style().setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell()
                .addStyle(style)
                .add(new Paragraph("FOOBAR FILM FESTIVAL").setFont(localBold)));
            doc.add(table);

            PdfCanvas canvas = new PdfCanvas(pdfDoc.getLastPage().newContentStreamBefore(),
                pdfDoc.getLastPage().getResources(), pdfDoc);
            try (Canvas lastCanvas = new Canvas(canvas, pdfDoc.getLastPage().getPageSize())) {
                lastCanvas.setFontColor(new DeviceGray(0.75f))
                    .setFontSize(52)
                    .setFont(localFont)
                    .showTextAligned(new Paragraph("FOOBAR FILM FESTIVAL"), 297.5f, 421, pdfDoc.getNumberOfPages(),
                        TextAlignment.CENTER, VerticalAlignment.MIDDLE, (float) Math.PI / 4);
            }
        }
    }

    protected static class StationeryEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            int pageNum = pdfDoc.getPageNumber(((PdfDocumentEvent) event).getPage());

            try (PdfReader reader = new PdfReader(SOURCE);
                 PdfDocument srcDoc = new PdfDocument(reader)) {
                PdfFormXObject watermark = srcDoc.getFirstPage().copyAsFormXObject(pdfDoc);
                new PdfCanvas(pdfDoc.getPage(pageNum).newContentStreamBefore(),
                    pdfDoc.getPage(pageNum).getResources(), pdfDoc)
                    .addXObjectAt(watermark, 0, 0);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private void addCountryInfo(DatabaseConnection connection,
                                CountryInfo info,
                                Document doc) throws SQLException {
        doc.add(new Paragraph(info.getName()).setFont(bold));
        doc.add(new Paragraph("\n"));
        Set<Movie> movies = new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
        movies.addAll(SqlUtils.getMovies(connection, info.getId()));

        for (Movie movie : movies) {
            doc.add(new Paragraph(movie.getMovieTitle()).setFont(bold));
            if (movie.getOriginalTitle() != null) {
                doc.add(new Paragraph(movie.getOriginalTitle()).setFont(italic));
            }

            doc.add(new Paragraph(String.format("Year: %d; run length: %d minutes",
                movie.getYear(), movie.getDuration())).setFont(normal));
            doc.add(ElementFactory.getDirectorList(movie));
        }
    }
}
