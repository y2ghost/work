package study.ywork.doc.itext.page;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.List;

public class ContentParser extends DefaultHandler {
    private StringBuilder buf = new StringBuilder();
    private final Document doc;
    private final List<String> roles;
    private final PdfFont font;
    private String role;

    public ContentParser(Document doc, List<String> roles)
            throws IOException {
        this.doc = doc;
        this.roles = roles;
        font = PdfFontFactory.createFont("src/main/resources/fonts/FreeSans.ttf",
                PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
    }

    public void characters(char[] ch, int start, int length) {
        for (int i = start; i < start + length; i++) {
            if (ch[i] == '\n')
                buf.append(' ');
            else
                buf.append(ch[i]);
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("chapter".equals(qName)) {
            return;
        }

        role = roles.get(0);
        roles.remove(0);
    }

    public void endElement(String uri, String localName, String qName) {
        if ("chapter".equals(qName)) {
            return;
        }

        String s = buf.toString().trim();
        buf = new StringBuilder();

        if (!s.isEmpty()) {
            Paragraph p = new Paragraph(s).setFont(font);
            p.getAccessibilityProperties().setRole(role);
            p.setTextAlignment(TextAlignment.JUSTIFIED);
            doc.add(p);
        }
    }
}
