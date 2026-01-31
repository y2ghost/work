package study.ywork.doc.itext.action;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.w3c.dom.Element;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CreateOutlineTree {
    private static final String DEST = "createOutlineTree.pdf";
    private static final String DEST_XML = "createOutlineTree.xml";
    private static final String RESOURCE = "https://imdb.com/title/tt%s/";
    private static final String INFO = "app.alert('Movie produced in %s; run length: %s');";

    public static void main(String[] args)
            throws IOException, SQLException, TransformerException, ParserConfigurationException {
        new CreateOutlineTree().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest)
            throws IOException, SQLException, TransformerException, ParserConfigurationException {
        manipulatePdf2(dest);
        createXml(dest, DEST_XML);
    }

    private static void createXml(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = docFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = db.newDocument();
            Element root = doc.createElement("Bookmark");
            doc.appendChild(root);
            List<PdfOutline> outlines = pdfDoc.getOutlines(false).getAllChildren();

            for (PdfOutline outline : outlines) {
                Element el = doc.createElement("Title");
                Element el2 = doc.createElement("Link");
                Element el3 = doc.createElement("Info");
                el.setTextContent(outline.getTitle());
                el.setAttribute("ElementsNumber", outline.getContent().get(PdfName.Parent).toString().substring(47, 55));
                el2.setTextContent(outline.getAllChildren().get(0).getTitle());
                el3.setTextContent(outline.getAllChildren().get(1).getTitle());
                root.appendChild(el);
                el.appendChild(el2);
                el.appendChild(el3);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty("encoding", "utf-8");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "2");
            t.transform(new DOMSource(doc), new StreamResult(dest));
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void manipulatePdf2(String dest) throws IOException, SQLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            pdfDoc.getCatalog().setPageMode(PdfName.UseOutlines);
            PdfOutline root = pdfDoc.getOutlines(false);
            List<Movie> movies = SqlUtils.getMovies();
            pdfDoc.addNewPage();

            for (Movie movie : movies) {
                String title = movie.getMovieTitle();
                if ("3-Iron".equals(title)) {
                    title = "빈집";
                }

                PdfOutline movieBookmark = root.addOutline(title);
                PdfPage lastPage = pdfDoc.getLastPage();
                float topOfBookmark = doc.getRenderer().getCurrentArea().getBBox().getTop();
                movieBookmark.addAction(PdfAction.createGoTo(PdfExplicitDestination.createFitH(lastPage, topOfBookmark)));
                PdfOutline link = movieBookmark.addOutline("link to IMDB");
                link.setStyle(PdfOutline.FLAG_BOLD);
                link.setColor(ColorConstants.BLUE);
                link.addAction(PdfAction.createURI((String.format(RESOURCE, movie.getImdb()))));
                PdfOutline info = movieBookmark.addOutline("instant info");
                info.addAction(PdfAction.createJavaScript(
                        String.format(INFO, movie.getYear(), movie.getDuration())));
                doc.add(new Paragraph(movie.getMovieTitle()));
                doc.add(ElementFactory.getDirectorList(movie));
                doc.add(ElementFactory.getCountryList(movie));
            }
        }
    }
}
