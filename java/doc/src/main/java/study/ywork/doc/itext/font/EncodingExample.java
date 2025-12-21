package study.ywork.doc.itext.font;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.font.PdfSimpleFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class EncodingExample {
    private static final String DEST = "encodingExample.pdf";
    protected static final String FONT = "src/main/resources/fonts/FreeSansBold.ttf";
    protected static final String[][] MOVIES = {
        {
            "Cp1252",
            "A Very long Engagement (France)",
            "directed by Jean-Pierre Jeunet",
            "Un long dimanche de fiançailles"
        },
        {
            "Cp1250",
            "No Man's Land (Bosnia-Herzegovina)",
            "Directed by Danis Tanovic",
            "Nikogaršnja zemlja"
        },
        {
            "Cp1251",
            "You I Love (Russia)",
            "directed by Olga Stolpovskaja and Dmitry Troitsky",
            "Я люблю тебя"
        },
        {
            "Cp1253",
            "Brides (Greece)",
            "directed by Pantelis Voulgaris",
            "Νύφες"
        }
    };

    public static void main(String[] args) {
        new EncodingExample().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font;
            for (int i = 0; i < 4; i++) {
                font = PdfFontFactory.createFont(FONT, MOVIES[i][0], EmbeddingStrategy.PREFER_EMBEDDED);
                doc.add(new Paragraph("Font: " + font.getFontProgram().getFontNames().getFontName()
                    + " with encoding: " + ((PdfSimpleFont) font).getFontEncoding().getBaseEncoding()));
                doc.add(new Paragraph(MOVIES[i][1]));
                doc.add(new Paragraph(MOVIES[i][2]));
                doc.add(new Paragraph(MOVIES[i][3]).setFont(font).setFontSize(12));
                doc.add(new Paragraph("\n"));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
