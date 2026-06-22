package study.ywork.doc.itext.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.IOException;

public class Diacritics1 {
    private static final String DEST = "diacritics1.pdf";
    private static final String MOVIE = "ฟ้าทะลายโจร";
    private static final String POSTER = "src/main/resources/images/posters/0269217.jpg";
    private static final String[] FONTS = {
        "src/main/resources/fonts/NotoSansThai-Regular.ttf",
        "src/main/resources/fonts/NotoSerifThai-Regular.ttf"
    };

    public static void main(String[] args) {
        new Diacritics1().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font;
            Image img = new Image(ImageDataFactory.create(POSTER));
            img.scale(0.5f, 0.5f);
            img.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 18f));
            img.setHorizontalAlignment(HorizontalAlignment.LEFT);
            doc.add(img);
            doc.add(new Paragraph(
                "Movie title: Tears of the Black Tiger (Thailand)"));
            doc.add(new Paragraph("directed by Wisit Sasanatieng"));
            for (int i = 0; i < 2; i++) {
                font = PdfFontFactory.createFont(FONTS[i], PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
                doc.add(new Paragraph("Font: " + font.getFontProgram().getFontNames().getFontName()));
                doc.add(new Paragraph(MOVIE).setFont(font).setFontSize(20));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
