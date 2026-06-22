package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;

public final class EmbeddedFonts {
    private EmbeddedFonts() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            String dir = "src/main/resources/fonts";
            PDType0Font font = PDType0Font.load(document, new File(dir + "LiberationSans-Regular.ttf"));

            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(font, 12);
                stream.setLeading(12 * 1.2f);
                stream.newLineAtOffset(50, 600);
                stream.showText("PDFBox's Unicode with Embedded TrueType Font");
                stream.newLine();
                stream.showText("Supports full Unicode text ☺");
                stream.newLine();
                stream.showText("English русский язык Tiếng Việt");
                stream.newLine();
                stream.showText("Ligatures: ﬁlm ﬂood");
                stream.endText();
            }

            document.save("example.pdf");
        }
    }
}
