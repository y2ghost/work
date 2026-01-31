package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 使用Type 1字体(.pfb)
 */
public final class HelloWorldType1 {
    private HelloWorldType1() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("usage: " + HelloWorldType1.class.getName() +
                    " <output-file> <Message> <pfb-file>");
            System.exit(1);
        }

        String file = args[0];
        String message = args[1];
        String pfbPath = args[2];

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            PDFont font;
            try (InputStream is = new FileInputStream(pfbPath)) {
                font = new PDType1Font(doc, is);
            }

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.beginText();
                contents.setFont(font, 12);
                contents.newLineAtOffset(100, 700);
                contents.showText(message);
                contents.endText();
            }

            doc.save(file);
            System.out.println(file + " created!");
        }
    }
}
