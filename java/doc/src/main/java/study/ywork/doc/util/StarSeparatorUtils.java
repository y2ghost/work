package study.ywork.doc.util;

import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

public class StarSeparatorUtils {
    public static Div newStartDiv() {
        Div div = new Div();
        Paragraph[] content = {new Paragraph("*"), new Paragraph("*  *")};
        for (Paragraph p : content) {
            p.setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMargins(0, 0, 0, 0);
            div.add(p);
        }

        content[1].setMarginTop(-4);
        div.setMargins(0, 0, 0, 0);
        return div;
    }

    private StarSeparatorUtils() {
    }
}
