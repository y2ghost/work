package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.io.IOException;

public class ExtraCharSpace {
    private static final String DEST = "extraCharSpace.pdf";
    private static final String MOVIE = "Aanrijding in Moscou";

    public static void main(String[] args) {
        new ExtraCharSpace().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font1 = PdfFontFactory.createFont("src/main/resources/fonts/FreeSans.ttf",
                PdfEncodings.CP1252, EmbeddingStrategy.PREFER_EMBEDDED);
            doc.add(new Paragraph("Movie title: Moscou, Belgium").setFont(font1));
            doc.add(new Paragraph("directed by Christophe Van Rompaey").setFont(font1));
            Text text = new Text(MOVIE).setFont(font1);
            text.setCharacterSpacing(10);
            doc.add(new Paragraph(text));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
