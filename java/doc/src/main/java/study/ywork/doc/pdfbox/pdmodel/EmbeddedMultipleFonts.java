package study.ywork.doc.pdfbox.pdmodel;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmbeddedMultipleFonts {
    private EmbeddedMultipleFonts() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = new PDDocument();
             TrueTypeCollection ttc2 = new TrueTypeCollection(new File("c:/windows/fonts/batang.ttc"));
             TrueTypeCollection ttc3 = new TrueTypeCollection(new File("c:/windows/fonts/mingliu.ttc"))) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont font1 = new PDType1Font(FontName.HELVETICA);
            // 朝鲜文
            PDType0Font font2 = PDType0Font.load(document, ttc2.getFontByName("Batang"), true);
            // 中文
            PDType0Font font3 = PDType0Font.load(document, ttc3.getFontByName("MingLiU"), true);
            // 印度字体
            PDType0Font font4 = PDType0Font.load(document, new File("c:/windows/fonts/mangal.ttf"));
            // 默认兼容字体
            PDType0Font font5 = PDType0Font.load(document, new File("c:/windows/fonts/ArialUni.ttf"));

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                cs.beginText();
                List<PDFont> fonts = new ArrayList<>();
                fonts.add(font1);
                fonts.add(font2);
                fonts.add(font3);
                fonts.add(font4);
                fonts.add(font5);
                cs.newLineAtOffset(20, 700);
                showTextMultiple(cs, "abc 한국 中国 भारत 日本 abc", fonts, 20);
                cs.endText();
            }

            document.save("example.pdf");
        }
    }

    static void showTextMultiple(PDPageContentStream cs, String text, List<PDFont> fonts, float size)
            throws IOException {
        try {
            fonts.get(0).encode(text);
            cs.setFont(fonts.get(0), size);
            cs.showText(text);
            return;
        } catch (IllegalArgumentException ex) {
            // NOOP
        }

        int i = 0;
        while (i < text.length()) {
            boolean found = false;
            for (PDFont font : fonts) {
                try {
                    String s = text.substring(i, i + 1);
                    font.encode(s);
                    int j = i + 1;

                    for (; j < text.length(); ++j) {
                        String s2 = text.substring(j, j + 1);

                        if (isWinAnsiEncoding(s2.codePointAt(0)) && font != fonts.get(0)) {
                            break;
                        }
                        try {
                            font.encode(s2);
                        } catch (IllegalArgumentException ex) {
                            break;
                        }
                    }

                    s = text.substring(i, j);
                    cs.setFont(font, size);
                    cs.showText(s);
                    i = j;
                    found = true;
                    break;
                } catch (IllegalArgumentException ex) {
                    // NOOP
                }
            }

            if (!found) {
                throw new IllegalArgumentException("Could not show '" + text.substring(i, i + 1)
                        + "' with the fonts provided");
            }
        }
    }

    static boolean isWinAnsiEncoding(int unicode) {
        String name = GlyphList.getAdobeGlyphList().codePointToName(unicode);
        if (".notdef".equals(name)) {
            return false;
        }
        return WinAnsiEncoding.INSTANCE.contains(name);
    }
}
