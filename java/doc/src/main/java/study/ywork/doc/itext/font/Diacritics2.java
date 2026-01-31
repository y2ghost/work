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

public class Diacritics2 {
    private static final String DEST = "diacritics2.pdf";
    private static final String MOVIE = "Tomten ¨ar far till alla barnen";
    private static final String[] FONTS = {
        "src/main/resources/fonts/FreeSans.ttf",
        "src/main/resources/fonts/LiberationMono-Regular.ttf"
    };

    public static void main(String[] args) {
        new Diacritics2().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph("Movie title: In Bed With Santa (Sweden)"));
            doc.add(new Paragraph("directed by Kjell Sundvall"));
            PdfFont f = PdfFontFactory.createFont(FONTS[0], PdfEncodings.CP1252, EmbeddingStrategy.PREFER_EMBEDDED);
            f.getGlyph('\u00a8').setXAdvance((short) -450);
            doc.add(new Paragraph(MOVIE).setFont(f));
            f = PdfFontFactory.createFont(FONTS[1], PdfEncodings.CP1252, EmbeddingStrategy.PREFER_EMBEDDED);
            f.getGlyph('\u00a8').setXAdvance((short) -600);
            doc.add(new Paragraph(MOVIE).setFont(f));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
