package study.ywork.doc.itext.action;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import org.w3c.dom.Element;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class LinkActions {
    private static final String SRC = "src/main/resources/pdfs/movieLinks1.pdf";
    private static final String SRC_RELATIVE = SRC;
    private static final String DEST = "linkActions.pdf";
    private static final String DEST_XML = "linkActions.xml";

    public static void main(String[] args) {
        new LinkActions().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        manipulatePdf2(dest);
        createXml(SRC, DEST_XML);
    }

    public static void createXml(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = docFactory.newDocumentBuilder();

            org.w3c.dom.Document doc = db.newDocument();
            Element root = doc.createElement("Destination");
            doc.appendChild(root);

            Map<String, PdfObject> names = pdfDoc.getCatalog().getNameTree(PdfName.Dests).getNames();
            for (Map.Entry<String, PdfObject> name : names.entrySet()) {
                Element el = doc.createElement("Name");
                el.setAttribute("Page", name.getValue().toString());
                el.setTextContent(name.getKey());
                root.appendChild(el);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty("encoding", "utf-8");
            t.setOutputProperty(OutputKeys.INDENT, "yes");

            t.transform(new DOMSource(doc), new StreamResult(dest));
        } catch (ParserConfigurationException | IOException | TransformerException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void manipulatePdf2(String dest) {
        DatabaseConnection connection;
        try {
            connection = DBUtils.getFilmDBConnection();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return;
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc);
             Statement stm = connection.createStatement()) {
            Paragraph p = new Paragraph("Click on a country, and you'll get a list of movies, containing links to the ")
                    .add(new Link("Internet Movie Database", PdfAction.createURI("https://www.imdb.com")))
                    .add(".");
            doc.add(p);

            p = new Paragraph("This list can be found in a ")
                    .add(new Link("separate document", PdfAction.createGoToR(SRC_RELATIVE, 1)))
                    .add(".");
            doc.add(p);

            ResultSet rs = stm.executeQuery("SELECT DISTINCT mc.country_id, c.country, count(*) AS c "
                    + "FROM film_country c, film_movie_country mc WHERE c.id = mc.country_id "
                    + "GROUP BY mc.country_id, country ORDER BY c DESC");
            while (rs.next()) {
                Paragraph country = new Paragraph(rs.getString("country"));
                country.add(": ");
                Link link = new Link(String.format("%d movies", rs.getInt("c")),
                        PdfAction.createGoToR(SRC_RELATIVE, rs.getString("country_id"), true));
                country.add(link);
                doc.add(country);
            }

            p = new Paragraph("Go to ")
                    .add(new Link("top", PdfAction.createGoTo("top")))
                    .add(".");
            doc.add(p);

            pdfDoc.addNamedDestination("top", PdfExplicitDestination.createXYZ(pdfDoc.getPage(1), 36, 842, 1).getPdfObject());
        } catch (SQLException | FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
