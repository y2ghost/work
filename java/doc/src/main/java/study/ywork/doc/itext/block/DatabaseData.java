package study.ywork.doc.itext.block;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseData {
    private static final String DEST = "dbData.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        new DatabaseData().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        List<Paragraph> paragraphList = new ArrayList<>();
        DatabaseConnection connection = DBUtils.getFilmDBConnection();

        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT country FROM film_country ORDER BY country");
            while (rs.next()) {
                paragraphList.add(new Paragraph(rs.getString("country")));
            }
        }

        connection.close();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            paragraphList.forEach(doc::add);
        }
    }
}
