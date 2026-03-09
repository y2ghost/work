package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BaseDirection;

import java.io.IOException;

public class RightToLeftExample {
    private static final String DEST = "rightToLeftExample.pdf";
    private static final String MOVIE = "האסונות של נינה";
    private static final String FONT = "src/main/resources/fonts/FreeSans.ttf";

    public static void main(String[] args) {
        new RightToLeftExample().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document document = new Document(pdfDoc, PageSize.A4)) {
            PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
            document.add(new Paragraph("Movie title: Nina's Tragedies"));
            document.add(new Paragraph("directed by Savi Gabizon"));
            document.add(new Paragraph(MOVIE).setFont(font).setFontSize(14).setBaseDirection(BaseDirection.RIGHT_TO_LEFT));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
