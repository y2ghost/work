package study.ywork.doc.itext.pdftool;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import study.ywork.doc.domain.CountryInfo;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.MovieComparator;
import study.ywork.doc.util.SqlUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class TwoPasses {
    private static final String DEST = "twoPasses.pdf";
    protected PdfFont bold;
    protected PdfFont italic;
    protected PdfFont normal;

    public static void main(String[] args) throws IOException, SQLException {
        new TwoPasses().manipulatePdf();
    }

    public static Table getHeaderTable(int x, int y) {
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setWidth(523);
        Style style = new Style()
            .setBorder(Border.NO_BORDER)
            .setHeight(20)
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorderBottom(new SolidBorder(1));
        table.setBorder(Border.NO_BORDER);
        table.addCell(new Cell()
            .add(new Paragraph("FOOBAR FILMFESTIVAL")).addStyle(style));
        table.addCell(new Cell()
            .add(new Paragraph(String.format("Page %d of %d", x, y))).addStyle(style));
        return table;
    }

    public void manipulatePdf() throws IOException, SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4));
             Statement stm = connection.createStatement()) {
            doc.setMargins(54, 36, 36, 36);
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
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())),
            new PdfWriter(DEST))) {
            int n = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= n; i++) {
                try (Canvas temp = new Canvas(new PdfCanvas(pdfDoc.getPage(i)),
                    new Rectangle(36, 803, 523, 30))) {
                    temp.add(getHeaderTable(i, n));
                }
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
            if (movie.getOriginalTitle() != null)
                doc.add(new Paragraph(movie.getOriginalTitle()).setFont(italic));
            doc.add(new Paragraph(String.format("Year: %d; run length: %d minutes",
                movie.getYear(), movie.getDuration())).setFont(normal));
            doc.add(ElementFactory.getDirectorList(movie));
        }
    }
}
