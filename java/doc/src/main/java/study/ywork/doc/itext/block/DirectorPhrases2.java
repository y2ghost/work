package study.ywork.doc.itext.block;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
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

public class DirectorPhrases2 {
    private static final String DEST = "directorPhrases2.pdf";
    private static final String FREE_SCANS_BOLD_PATH = "src/main/resources/fonts/FreeSansBold.ttf";
    private static final String FREE_SCANS_PATH = "src/main/resources/fonts/FreeSans.ttf";

    public static void main(String[] args) throws IOException, SQLException {
        new DirectorPhrases2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        PdfFont timesbd = null;
        PdfFont times = null;

        try {
            timesbd = PdfFontFactory.createFont(FREE_SCANS_BOLD_PATH, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
            times = PdfFontFactory.createFont(FREE_SCANS_PATH, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        List<Paragraph> paragraphList = new ArrayList<>();
        DatabaseConnection connection = DBUtils.getFilmDBConnection();

        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT name, given_name FROM film_director ORDER BY name, given_name");
            while (rs.next()) {
                paragraphList.add(createDirectorPhrase(rs, timesbd, times));
            }
        }

        connection.close();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            paragraphList.forEach(doc::add);
        }
    }

    public Paragraph createDirectorPhrase(ResultSet rs, PdfFont timesbd, PdfFont times)
            throws SQLException {
        Paragraph director = new Paragraph();
        Text name = new Text(rs.getString("name")).setFont(timesbd);
        name.setUnderline(0.2f, -2f);
        director.add(name);
        director.add(new Text(",").setFont(timesbd));
        director.add(new Text(" ").setFont(times));
        director.add(new Text(rs.getString("given_name")).setFont(times));
        director.setFixedLeading(24);
        return director;
    }
}
