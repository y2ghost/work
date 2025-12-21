package study.ywork.doc.itext.page;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StructuredContent {
    private static final String DEST = "structuredContent.pdf";
    private static final String RESOURCE = "src/main/resources/xml/moby.xml";

    public static void main(String[] args) {
        new StructuredContent().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            pdfDoc.setDefaultPageSize(PageSize.A5);
            pdfDoc.setTagged();
            PdfStructTreeRoot root = pdfDoc.getStructTreeRoot();
            root.addRoleMapping("chapter", StandardRoles.SECT);
            root.addRoleMapping("title", StandardRoles.H);
            root.addRoleMapping("para", StandardRoles.P);

            TagTreePointer autoTaggingPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            autoTaggingPointer.addTag("chapter");
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            List<String> roles = new ArrayList<>();
            parser.parse(
                    new InputSource(new FileInputStream(RESOURCE)),
                    new StructureParser(roles));
            parser.parse(
                    new InputSource(new FileInputStream(RESOURCE)),
                    new ContentParser(doc, roles));
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
