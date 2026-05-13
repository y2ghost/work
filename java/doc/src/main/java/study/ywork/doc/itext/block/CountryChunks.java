package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CountryChunks {
    private static final String DEST = "countryChunks.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        new CountryChunks().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        try (Document doc = new Document(pdfDoc)) {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            DatabaseConnection connection = DBUtils.getFilmDBConnection();

            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery("SELECT country, id FROM film_country ORDER BY country");
                while (rs.next()) {
                    Paragraph p = new Paragraph().setFixedLeading(16);
                    p.add(new Text(rs.getString("country")));
                    p.add(new Text(" "));
                    Text id = new Text(rs.getString("id")).setFont(font).setFontSize(6).setFontColor(ColorConstants.WHITE);
                    id.setBackgroundColor(ColorConstants.BLACK, 1f, 0.5f, 1f, 1.5f).setTextRise(6);
                    p.add(id);
                    doc.add(p);
                }
            }

            connection.close();
        }
    }
}
