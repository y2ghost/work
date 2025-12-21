package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
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
import java.util.ArrayList;
import java.util.List;

public class DirectorPhrases1 {
    private static final String DEST = "directorPhrases1.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        new DirectorPhrases1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {


        List<Paragraph> paragraphList = new ArrayList<>();
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT name, given_name FROM film_director ORDER BY name, given_name");

            while (rs.next()) {
                paragraphList.add(createDirectorPhrase(rs));
            }

        }

        connection.close();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            paragraphList.forEach(doc::add);
        }
    }

    public Paragraph createDirectorPhrase(ResultSet rs)
            throws SQLException, IOException {
        PdfFont boldUnderlined = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        Paragraph director = new Paragraph();
        director.add(new Text(rs.getString("name"))
                .setFont(boldUnderlined)
                .setFontSize(12)
                .setUnderline());
        director.add(new Text(",")
                .setFont(boldUnderlined)
                .setFontSize(12)
                .setUnderline());
        director.add(new Text(" ")
                .setFont(normal)
                .setFontSize(12));
        director.add(new Text(rs.getString("given_name"))
                .setFont(normal)
                .setFontSize(12));
        return director;
    }
}
