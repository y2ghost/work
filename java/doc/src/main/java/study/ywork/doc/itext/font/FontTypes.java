package study.ywork.doc.itext.font;

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeCollection;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class FontTypes {
    private static final String DEST = "fontTypes.pdf";
    private static final String TEXT = "quick brown fox jumps over the lazy dog\nQUICK BROWN FOX JUMPS OVER THE LAZY DOG";
    private static final String[][] FONTS = {
        {StandardFonts.HELVETICA, PdfEncodings.WINANSI},
        {"src/main/resources/fonts/cmr10.afm", PdfEncodings.WINANSI},
        {"src/main/resources/fonts/cmr10.pfm", FontEncoding.FONT_SPECIFIC},
        {"src/main/resources/fonts/FreeSans.ttf", PdfEncodings.WINANSI},
        {"src/main/resources/fonts/FreeSans.ttf", PdfEncodings.IDENTITY_H},
        {"src/main/resources/fonts/Puritan2.otf", PdfEncodings.WINANSI},
        {"src/main/resources/fonts/ipam.ttc", PdfEncodings.IDENTITY_H},
        {"KozMinPro-Regular", "UniJIS-UCS2-H"}
    };

    public static void main(String[] args) {
        new FontTypes().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font;
            for (String[] strings : FONTS) {
                if (strings[0].endsWith(".ttc")) {
                    TrueTypeCollection coll = new TrueTypeCollection(strings[0]);
                    font = PdfFontFactory.createFont(coll.getFontByTccIndex(0), strings[1], EmbeddingStrategy.PREFER_EMBEDDED);
                } else {
                    font = PdfFontFactory.createFont(strings[0], strings[1], EmbeddingStrategy.PREFER_EMBEDDED);
                }

                doc.add(new Paragraph(String.format("Font file: %s with encoding %s", strings[0], strings[1])));
                doc.add(new Paragraph(String.format("iText class: %s", font.getClass().getName())));
                doc.add(new Paragraph(TEXT).setFont(font).setFontSize(12));
                ILineDrawer line = new SolidLine(0.5f);
                doc.add(new LineSeparator(line));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
