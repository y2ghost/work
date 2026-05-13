package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.TextAlignment;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SayPeace {
    private static final String DEST = "sayPeace.pdf";
    private static final String FONT = "src/main/resources/fonts/FreeSans.ttf";
    private static final String ARABIC_FONT = "src/main/resources/fonts/NotoNaskhArabic-Regular.ttf";
    private static final String RESOURCE = "src/main/resources/xml/say_peace.xml";
    private static final Pattern PATTERN = Pattern.compile("\\p{InArabic}");

    public static void main(String[] args) {
        new SayPeace().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document document = new Document(pdfDoc, PageSize.A4)) {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(new FileInputStream(RESOURCE)), new CustomHandler(document));
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static class CustomHandler extends DefaultHandler {
        private StringBuilder buf = new StringBuilder();
        private Cell cell;
        private Table table;
        private Document document;
        private PdfFont f;
        private PdfFont arabicF;

        public CustomHandler(Document document) {
            this.document = document;
            try {
                this.f = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
                this.arabicF = PdfFontFactory.createFont(ARABIC_FONT, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) {
            if ("message".equals(qName)) {
                buf = new StringBuilder();
                cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                if ("RTL".equals(attributes.getValue("direction"))) {
                    cell.setBaseDirection(BaseDirection.RIGHT_TO_LEFT).
                        setTextAlignment(TextAlignment.RIGHT).
                        setFont(f);
                } else {
                    cell.setFont(f);
                }
            } else if ("pace".equals(qName)) {
                table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
            }
        }

        public void endElement(String uri, String localName, String qName) {
            if ("big".equals(qName)) {
                String txt = strip(buf);
                Text bold = new Text(txt);
                if (isArabic(txt)) {
                    bold.setFontScript(Character.UnicodeScript.ARABIC);
                    bold.setFont(arabicF);
                }

                bold.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE).
                    setStrokeWidth(0.5f).
                    setStrokeColor(DeviceGray.BLACK);
                Paragraph p = new Paragraph(bold);
                cell.add(p);
                buf = new StringBuilder();
            } else if ("message".equals(qName)) {
                String txt = strip(buf);
                Paragraph p = new Paragraph(strip(buf));
                if (isArabic(txt)) {
                    p.setFontScript(Character.UnicodeScript.ARABIC);
                    p.setFont(arabicF);
                }
                cell.add(p);
                table.addCell(cell);
                buf = new StringBuilder();
            } else if ("pace".equals(qName)) {
                document.add(table);
            }
        }

        public void characters(char[] ch, int start, int length) {
            buf.append(ch, start, length);
        }

        protected String strip(StringBuilder buf) {
            int pos;
            while ((pos = buf.indexOf("\n")) != -1)
                buf.replace(pos, pos + 1, " ");
            while (buf.charAt(0) == ' ')
                buf.deleteCharAt(0);
            return buf.toString();
        }

        private static boolean isArabic(String text) {
            Matcher matcher = PATTERN.matcher(text);
            return matcher.find();
        }
    }
}
