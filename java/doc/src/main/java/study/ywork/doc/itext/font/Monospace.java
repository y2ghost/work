package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.font.PdfSimpleFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class Monospace {
    private static final String DEST = "monospace.pdf";
    private static final String MOVIE = "Aanrijding in Moscou";

    public static void main(String[] args) {
        new Monospace().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font1 = PdfFontFactory.createFont("src/main/resources/fonts/FreeSans.ttf",
                PdfEncodings.CP1252, EmbeddingStrategy.PREFER_EMBEDDED);
            doc.add(new Paragraph("Movie title: Moscou, Belgium").setFont(font1));
            doc.add(new Paragraph("directed by Christophe Van Rompaey").setFont(font1));
            doc.add(new Paragraph(MOVIE).setFont(font1));
            PdfFont font2 = PdfFontFactory.createFont("src/main/resources/fonts/LiberationMono-Regular.ttf",
                PdfEncodings.CP1252, EmbeddingStrategy.PREFER_EMBEDDED);
            doc.add(new Paragraph(MOVIE).setFont(font2));
            PdfFont font3 = PdfFontFactory.createFont("src/main/resources/fonts/FreeSansBold.ttf",
                PdfEncodings.CP1252, EmbeddingStrategy.PREFER_EMBEDDED);
            int[] widths = font3.getFontProgram().getFontMetrics().getGlyphWidths();
            for (int k = 0; k < widths.length; ++k) {
                if (widths[k] != 0)
                    widths[k] = 600;
            }
            ((PdfSimpleFont) font3).setForceWidthsOutput(true);
            doc.add(new Paragraph(MOVIE).setFont(font3));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
