package study.ywork.doc.itext.page;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class StructureParser extends DefaultHandler {
    private final List<String> roles;

    public StructureParser(List<String> roles) {
        this.roles = roles;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("chapter".equals(qName)) {
            return;
        }

        roles.add(qName);
    }
}
