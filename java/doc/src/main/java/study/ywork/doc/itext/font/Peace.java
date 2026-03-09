package study.ywork.doc.itext.font;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.properties.UnitValue;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;

public class Peace {
    private static final String DEST = "peace.pdf";
    private static final String FONTS_FOLDER = "src/main/resources/fonts/font2/";
    private static final FontSet FONT_SET;

    static {
        FONT_SET = new FontSet();
        FONT_SET.addDirectory(FONTS_FOLDER);
    }

    private static final String RESOURCE = "src/main/resources/xml/peace.xml";

    public static void main(String[] args) {
        new Peace().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document document = new Document(pdfDoc, PageSize.A4)) {
            document.setFontProvider(new FontProvider(FONT_SET));
            document.setFontFamily("Noto Sans");

            Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(new FileInputStream(RESOURCE)), new CustomHandler(table));
            document.add(table);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static class CustomHandler extends DefaultHandler {
        private StringBuilder buf = new StringBuilder();
        private final Table tab;

        public CustomHandler(Table t) {
            tab = t;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes att) {
            if ("pace".equals(qName)) {
                tab.addCell(att.getValue("language"));
                tab.addCell(att.getValue("countries"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if ("pace".equals(qName)) {
                Paragraph para = new Paragraph();
                para.add(strip(buf));
                tab.addCell(para);
                buf = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            buf.append(ch, start, length);
        }

        private static String strip(StringBuilder buf) {
            int pos;
            while ((pos = buf.indexOf("\n")) != -1) {
                buf.replace(pos, pos + 1, " ");
            }
            return buf.toString().trim();
        }
    }
}
