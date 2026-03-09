package study.ywork.doc.itext.pdftool;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
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

public class StampStationery {
    private static final String DEST = "stampStationery.pdf";
    private static final String ORIGINAL = "stampStationery_original.pdf";
    private static final String STATIONERY_WATERMARK = "src/main/resources/pdfs/stationery_watermark.pdf";

    protected PdfFont bold;
    protected PdfFont italic;
    protected PdfFont normal;

    public static void main(String[] args) throws IOException, SQLException {
        new StampStationery().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        StampStationery stationary = new StampStationery();
        stationary.createPdf(ORIGINAL);
        stationary.manipulatePdf2(ORIGINAL, STATIONERY_WATERMARK, DEST);
    }

    public void manipulatePdf2(String src, String stationery, String dest) throws IOException {
        try (PdfReader stationeryReader = new PdfReader(stationery);
             PdfDocument stationeryDoc = new PdfDocument(stationeryReader);
             PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            int n = pdfDoc.getNumberOfPages();
            PdfFormXObject watermark = stationeryDoc.getFirstPage().copyAsFormXObject(pdfDoc);
            
            for (int i = 1; i <= n; i++) {
                PdfPage page = pdfDoc.getPage(i);
                new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc).addXObjectAt(watermark, 0, 0);
            }

        }
    }

    public void createPdf(String filename) throws SQLException, IOException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4));
             Statement stm = connection.createStatement()) {
            doc.setMargins(72, 36, 36, 36);
            bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
            normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
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
        }

        connection.close();
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
