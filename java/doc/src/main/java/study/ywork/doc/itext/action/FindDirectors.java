package study.ywork.doc.itext.action;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FindDirectors {
    private static final String DEST = "findDirectors.pdf";
    private static final String RESOURCE = "src/main/resources/js/find_director.js";
    private static final String NESTED_TABLES = "src/main/resources/pdfs/nestedTables.pdf";

    public static void main(String[] args) throws IOException {
        new FindDirectors().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        manipulatePdf2();
    }

    public void manipulatePdf2() throws IOException {
        List<Paragraph> paragraphs = new ArrayList<>();
        try {
            DatabaseConnection connection = DBUtils.getFilmDBConnection();
            try (Statement stm = connection.createStatement()) {
                ResultSet rs = stm.executeQuery(
                        "SELECT name, given_name FROM film_director ORDER BY name, given_name");
                while (rs.next()) {
                    paragraphs.add(createDirectorParagraph(rs));
                }
            }

            connection.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return;
        }

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(byteOut));
             Document doc = new Document(pdfDoc)) {
            paragraphs.forEach(doc::add);
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST))) {
            pdfDoc.initializeOutlines();
            pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(readFileToString(RESOURCE)));
            PdfDocument[] pdfDocuments = {
                    new PdfDocument(new PdfReader(new ByteArrayInputStream(byteOut.toByteArray()))),
                    new PdfDocument(new PdfReader(NESTED_TABLES)),
            };

            for (PdfDocument pdfDocument : pdfDocuments) {
                int n = pdfDocument.getNumberOfPages();
                pdfDocument.copyPagesTo(1, n, pdfDoc);
            }

            for (PdfDocument pdfDocument : pdfDocuments) {
                pdfDocument.close();
            }
        }
    }

    public Paragraph createDirectorParagraph(ResultSet rs) throws SQLException {
        String strName = rs.getString("name");
        PdfAction action = PdfAction.createJavaScript(String.format("findDirector('%s');", strName));
        String buffer = strName + ", " + rs.getString("given_name");
        Link name = new Link(buffer, action);
        return new Paragraph(name);
    }

    protected static String readFileToString(String path) throws IOException {
        Path jsPath = Path.of(path);
        byte[] jsBytes = Files.readAllBytes(jsPath);
        return new String(jsBytes).replace("\r\n", "\n");
    }
}
