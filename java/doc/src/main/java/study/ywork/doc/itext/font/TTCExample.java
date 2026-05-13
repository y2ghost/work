package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeCollection;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class TTCExample {
    private static final String DEST = "TTCExample.pdf";
    private static final String FONT = "src/main/resources/fonts/ipam.ttc";

    public static void main(String[] args) {
        new TTCExample().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font;
            TrueTypeCollection coll = new TrueTypeCollection(FONT);
            int i = 0;
            while (i < coll.getTTCSize()) {
                font = PdfFontFactory.createFont(coll.getFontByTccIndex(i), PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
                doc.add(new Paragraph("font " + i + ": " + coll.getFontByTccIndex(i).getFontNames().getFontName())
                    .setFont(font).setFontSize(12));
                doc.add(new Paragraph("Rashômon")
                    .setFont(font).setFontSize(12));
                doc.add(new Paragraph("Directed by Akira Kurosawa")
                    .setFont(font).setFontSize(12));
                doc.add(new Paragraph("羅生門")
                    .setFont(font).setFontSize(12));
                doc.add(new Paragraph("\n"));
                i++;
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
