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

public class Ligatures1 {
    private static final String DEST = "ligatures1.pdf";

    public static void main(String[] args) {
        new Ligatures1().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document document = new Document(pdfDoc)) {
            PdfFont font = PdfFontFactory.createFont("src/main/resources/fonts/FreeSans.ttf",
                PdfEncodings.CP1252, EmbeddingStrategy.PREFER_EMBEDDED);
            document.add(new Paragraph("Movie title: Love at First Hiccough (Denmark)").setFont(font));
            document.add(new Paragraph("directed by Tomas Villum Jensen").setFont(font));
            document.add(new Paragraph("Kærlighed ved første hik").setFont(font));
            document.add(new Paragraph(ligaturize("Kaerlighed ved f/orste hik")).setFont(font));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public String ligaturize(String s) {
        int pos;
        while ((pos = s.indexOf("ae")) > -1) {
            s = s.substring(0, pos) + 'æ' + s.substring(pos + 2);
        }

        while ((pos = s.indexOf("/o")) > -1) {
            s = s.substring(0, pos) + 'ø' + s.substring(pos + 2);
        }

        return s;
    }
}
