package study.ywork.doc.itext.font;

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.FontStyles;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.util.TreeSet;

public class FontFactory {
    private static final String DEST = "fontFactory.pdf";
    private static final String FONT_DIR = "src/main/resources/fonts/font1/";

    public static void main(String[] args) {
        new FontFactory().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        FontProgramFactory.clearRegisteredFonts();
        FontProgramFactory.clearRegisteredFontFamilies();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            doc.add(new Paragraph("Times-Roman").setFont(font));
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            doc.add(new Paragraph("Times-Roman, Bold").setFont(fontBold));
            doc.add(new Paragraph("\n"));

            FontProgramFactory.registerFont(FONT_DIR + "EBGaramond12-Regular.ttf", "my_bold_font");
            PdfFont myBoldFont = PdfFontFactory.createRegisteredFont("my_bold_font");
            doc.add(new Paragraph(font.getFontProgram().getFontNames().getFontName()).setFont(myBoldFont));
            String[][] name = myBoldFont.getFontProgram().getFontNames().getFullName();

            for (int i = 0; i < name.length; i++) {
                doc.add(new Paragraph(name[i][3] + " (" + name[i][0]
                    + "; " + name[i][1] + "; " + name[i][2] + ")"));
            }

            PdfFont myBoldFont2 = PdfFontFactory.createRegisteredFont("EB Garamond 12 Regular");
            doc.add(new Paragraph("EB Garamond 12").setFont(myBoldFont2));
            doc.add(new Paragraph("\n"));

            doc.add(new Paragraph("Registered fonts:"));
            FontProgramFactory.registerFontDirectory(FONT_DIR);

            for (String f : new TreeSet<>(FontProgramFactory.getRegisteredFonts())) {
                doc.add(new Paragraph(f).setFont(PdfFontFactory.createRegisteredFont(f, "",
                    EmbeddingStrategy.PREFER_EMBEDDED)));
            }

            doc.add(new Paragraph("\n"));
            PdfFont cmr10 = PdfFontFactory.createRegisteredFont("cmr10", FontEncoding.FONT_SPECIFIC,
                EmbeddingStrategy.PREFER_EMBEDDED);
            doc.add(new Paragraph("Computer Modern").setFont(cmr10));
            doc.add(new Paragraph("\n"));

            for (String f : new TreeSet<>(FontProgramFactory.getRegisteredFontFamilies())) {
                doc.add(new Paragraph(f));
            }
            doc.add(new Paragraph("\n"));

            String ebFont = "EB Garamond";
            PdfFont garamond = PdfFontFactory.createRegisteredFont(ebFont, PdfEncodings.WINANSI,
                EmbeddingStrategy.PREFER_EMBEDDED);
            doc.add(new Paragraph(ebFont).setFont(garamond));
            PdfFont garamondItalic = PdfFontFactory.createRegisteredFont(ebFont, PdfEncodings.WINANSI,
                EmbeddingStrategy.PREFER_EMBEDDED, FontStyles.ITALIC);
            doc.add(new Paragraph("EB Garamond Italic").setFont(garamondItalic).setFontSize(12));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
