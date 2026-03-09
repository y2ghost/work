package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;

/**
 * 使用TrueType字体
 */
public final class HelloWorldTTF {
    private HelloWorldTTF() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("usage: " + HelloWorldTTF.class.getName() +
                    " <output-file> <Message> <ttf-file>");
            System.exit(1);
        }

        String pdfPath = args[0];
        String message = args[1];
        String ttfPath = args[2];

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDFont font = PDType0Font.load(doc, new File(ttfPath));

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.beginText();
                contents.setFont(font, 12);
                contents.newLineAtOffset(100, 700);
                contents.showText(message);
                contents.endText();
            }

            doc.save(pdfPath);
            System.out.println(pdfPath + " created!");
        }
    }
}
