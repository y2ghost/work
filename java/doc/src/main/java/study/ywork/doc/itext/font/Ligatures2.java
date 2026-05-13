package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;

public class Ligatures2 {
    private static final String DEST = "ligatures2.pdf";
    private static final String MOVIE
        = "لورانس العرب";
    private static final String MOVIE_WITH_SPACES
        = "ل و ر ا ن س   ا ل ع ر ب";
    private static final String FONT = "src/main/resources/fonts/NotoNaskhArabic-Regular.ttf";

    public static void main(String[] args) {
        new Ligatures2().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document document = new Document(pdfDoc)) {
            PdfFont bf = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
            document.add(new Paragraph("Movie title: Lawrence of Arabia (UK)"));
            document.add(new Paragraph("directed by David Lean"));
            document.add(new Paragraph("Autodetect: " + MOVIE).setFont(bf).setFontSize(20));

            Style arabic = new Style().setTextAlignment(TextAlignment.RIGHT).setBaseDirection(BaseDirection.RIGHT_TO_LEFT).
                setFontSize(20).setFont(bf);

            document.add(new Paragraph("Wrong: " + MOVIE_WITH_SPACES).addStyle(arabic));
            document.add(new Paragraph(MOVIE).setFontScript(Character.UnicodeScript.ARABIC).addStyle(arabic));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
