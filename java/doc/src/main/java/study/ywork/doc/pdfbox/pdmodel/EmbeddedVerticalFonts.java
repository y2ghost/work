package study.ywork.doc.pdfbox.pdmodel;

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;

public class EmbeddedVerticalFonts {
    private EmbeddedVerticalFonts() {
    }

    public static void main(String[] args) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        // 下载字体 https://moji.or.jp/wp-content/ipafont/IPAfont/ipag00303.zip
        File ipafont = new File("src/main/resources/pdfbox/ttf/ipag.ttf");
        PDType0Font hfont = PDType0Font.load(document, ipafont);
        PDType0Font vfont = PDType0Font.loadVertical(document, ipafont);
        TrueTypeFont ttf = new TTFParser().parse(new RandomAccessReadBufferedFile(ipafont));
        PDType0Font vfont2 = PDType0Font.loadVertical(document, ttf, true);
        ttf.disableGsubFeature("vrt2");
        ttf.disableGsubFeature("vert");

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(hfont, 20);
            contentStream.setLeading(25);
            contentStream.newLineAtOffset(20, 300);
            contentStream.showText("Key:");
            contentStream.newLine();
            contentStream.showText("① Horizontal");
            contentStream.newLine();
            contentStream.showText("② Vertical with substitution");
            contentStream.newLine();
            contentStream.showText("③ Vertical without substitution");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(hfont, 20);
            contentStream.newLineAtOffset(20, 650);
            contentStream.showText("①「あーだこーだ」");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(vfont, 20);
            contentStream.newLineAtOffset(50, 600);
            contentStream.showText("②「あーだこーだ」");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(vfont2, 20);
            contentStream.newLineAtOffset(100, 600);
            contentStream.showText("③「あーだこーだ」");
            contentStream.endText();
        }

        document.save("vertical.pdf");
    }
}
