package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.font.PdfType0Font;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class UnicodeExample extends EncodingExample {
    private static final String DEST = "unicodeExample.pdf";

    public static void main(String[] args) {
        new UnicodeExample().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font;
            for (int i = 0; i < 4; i++) {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
                // IDENTITY_H结果在PdfType0Font和PdfType0Font中支持cmap编码
                doc.add(new Paragraph("Font: " + font.getFontProgram().getFontNames().getFontName()
                    + " with encoding: " + ((PdfType0Font) font).getCmap().getCmapName()));
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
