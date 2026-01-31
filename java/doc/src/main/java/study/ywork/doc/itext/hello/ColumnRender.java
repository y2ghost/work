package study.ywork.doc.itext.hello;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ColumnRender {
    private static final String DEST = "columnRender.pdf";
    private static final String APPLE_IMG = "src/main/resources/images/apple.jpg";
    private static final String APPLE_TXT = "src/main/resources/data/apple.txt";
    private static final String FACEBOOK_IMG = "src/main/resources/images/fb.jpg";
    private static final String FACEBOOK_TXT = "src/main/resources/data/fb.txt";
    private static final String INST_IMG = "src/main/resources/images/inst.jpg";
    private static final String INST_TXT = "src/main/resources/data/inst.txt";
    private static PdfFont timesNewRoman = null;
    private static PdfFont timesNewRomanBold = null;

    public static void main(String[] args) throws Exception {
        timesNewRoman = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        timesNewRomanBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        new ColumnRender().createPdf(DEST);
    }

    private void createPdf(String dest) {
        PageSize ps = PageSize.A5;
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
             Document document = new Document(pdf, ps)) {
            float offSet = 36;
            float columnWidth = (ps.getWidth() - offSet * 2 + 10) / 3;
            float columnHeight = ps.getHeight() - offSet * 2;

            Rectangle[] columns = {new Rectangle(offSet - 5, offSet, columnWidth, columnHeight),
                    new Rectangle(offSet + columnWidth, offSet, columnWidth, columnHeight),
                    new Rectangle(offSet + columnWidth * 2 + 5, offSet, columnWidth, columnHeight)};
            document.setRenderer(new ColumnDocumentRenderer(document, columns));

            Image apple = new Image(ImageDataFactory.create(APPLE_IMG)).setWidth(columnWidth);
            String articleApple = new String(Files.readAllBytes(Paths.get(APPLE_TXT)), StandardCharsets.UTF_8);
            ColumnRender.addArticle(document, "Apple Encryption Engineers, if Ordered to Unlock iPhone, Might Resist", "By JOHN MARKOFF MARCH 18, 2016", apple, articleApple);
            Image facebook = new Image(ImageDataFactory.create(FACEBOOK_IMG)).setWidth(columnWidth);
            String articleFB = new String(Files.readAllBytes(Paths.get(FACEBOOK_TXT)), StandardCharsets.UTF_8);
            ColumnRender.addArticle(document, "With \"Smog Jog\" Through Beijing, Zuckerberg Stirs Debate on Air Pollution", "By PAUL MOZUR MARCH 18, 2016", facebook, articleFB);
            Image inst = new Image(ImageDataFactory.create(INST_IMG)).setWidth(columnWidth);
            String articleInstagram = new String(Files.readAllBytes(Paths.get(INST_TXT)), StandardCharsets.UTF_8);
            ColumnRender.addArticle(document, "Instagram May Change Your Feed, Personalizing It With an Algorithm", "By MIKE ISAAC MARCH 15, 2016", inst, articleInstagram);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void addArticle(Document doc, String title, String author, Image img, String text) {
        Paragraph p1 = new Paragraph(title)
                .setFont(timesNewRomanBold)
                .setFontSize(14);
        doc.add(p1);
        doc.add(img);
        Paragraph p2 = new Paragraph()
                .setFont(timesNewRoman)
                .setFontSize(7)
                .setFontColor(ColorConstants.GRAY)
                .add(author);
        doc.add(p2);
        Paragraph p3 = new Paragraph()
                .setFont(timesNewRoman)
                .setFontSize(10)
                .add(text);
        doc.add(p3);
    }
}
