package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FontFileAndSizes {
    private static final String[] RESULT = {
        "fontFileAndSizes_font_not_embedded.pdf",
        "fontFileAndSizes_font_embedded.pdf",
        "fontFileAndSizes_font_embedded_less_glyphs.pdf",
        "fontFileAndSizes_font_compressed.pdf",
        "fontFileAndSizes_font_full.pdf",
    };
    private static final String FONT = "src/main/resources/fonts/FreeSans.ttf";
    private static final String TEXT1 = "quick brown fox jumps over the lazy dog";
    private static final String TEXT2 = "ooooo ooooo ooo ooooo oooo ooo oooo ooo";

    public static void main(String[] args) {
        new FontFileAndSizes().manipulatePdf();
    }

    protected void manipulatePdf() {
        List<FontHelper> helpers = new ArrayList<>(RESULT.length);
        try {
            PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            FontHelper helper = new FontHelper(font, TEXT1);
            helpers.add(helper);

            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
            helper = new FontHelper(font, TEXT1);
            helpers.add(helper);

            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
            helper = new FontHelper(font, TEXT2);
            helpers.add(helper);


            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
            helper = new FontHelper(font, TEXT1);
            helpers.add(helper);

            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
            font.setSubset(false);
            helper = new FontHelper(font, TEXT1);
            helpers.add(helper);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return;
        }

        for (int i = 0; i < helpers.size(); i++) {
            try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(RESULT[i]))) {
                FontHelper helper = helpers.get(i);
                writeAndClosePdf(pdfDoc, helper.getFont(), helper.getText());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public void writeAndClosePdf(PdfDocument pdfDoc, PdfFont font, String text) {
        try (Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph(text).setFont(font).setFontSize(12));
        }
    }

    private static class FontHelper {
        private final PdfFont font;
        private final String text;

        public FontHelper(PdfFont font, String text) {
            this.font = font;
            this.text = text;
        }

        public PdfFont getFont() {
            return font;
        }

        public String getText() {
            return text;
        }
    }
}
