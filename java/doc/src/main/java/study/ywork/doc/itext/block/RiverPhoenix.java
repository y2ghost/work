package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

public class RiverPhoenix {
    private static final String DEST = "riverPhoenix.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";

    private final PdfFont bold;

    public RiverPhoenix() throws IOException {
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    public static void main(String args[]) throws IOException, SQLException {
        new RiverPhoenix().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        //Initialize document
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph("Movies featuring River Phoenix").setFont(bold));
            doc.add(createParagraph(
                    "My favorite movie featuring River Phoenix was ", "0092005"));
            doc.add(createParagraph(
                    "River Phoenix was nominated for an academy award for his role in ", "0096018"));
            doc.add(createParagraph(
                    "River Phoenix played the young Indiana Jones in ", "0097576"));
            doc.add(createParagraph(
                    "His best role was probably in ", "0102494"));
        }
    }

    public Paragraph createParagraph(String text, String imdb) throws MalformedURLException {
        Paragraph p = new Paragraph(text);
        Image img = new Image(ImageDataFactory.create(String.format(RESOURCE, imdb)));
        img.scaleToFit(1000, 72);
        img.setRotationAngle(Math.toRadians(-30));
        p.add(img);
        return p;
    }
}
