package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class EncodingNames {
    private static final String DEST = "encodingNames.pdf";
    private static final String[] FONT = {
        "src/main/resources/fonts/Puritan2.otf",
        "src/main/resources/fonts/FreeSans.ttf",
    };

    public static void main(String[] args) {
        new EncodingNames().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            showEncodings(doc, FONT[0]);
            doc.add(new AreaBreak());
            showEncodings(doc, FONT[1]);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void showEncodings(Document doc, String fontConstant) throws IOException {
        PdfFont font = PdfFontFactory.createFont(fontConstant, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        doc.add(new Paragraph("PostScript name: " + font.getFontProgram().getFontNames().getFontName()));
        doc.add(new Paragraph("Available code pages:"));
        String[] encoding = ((TrueTypeFont) font.getFontProgram()).getCodePagesSupported();

        for (int i = 0; i < encoding.length; i++) {
            doc.add(new Paragraph("encoding[" + i + "] = " + encoding[i]));
        }
    }
}
