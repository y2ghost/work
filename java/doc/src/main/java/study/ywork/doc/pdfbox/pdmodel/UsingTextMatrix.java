package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.util.Matrix;

import java.io.IOException;

public class UsingTextMatrix {
    public UsingTextMatrix() {
        // NOOP
    }

    public void doIt(String message, String outfile) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDFont font = new PDType1Font(FontName.HELVETICA);
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            float fontSize = 12.0f;

            PDRectangle pageSize = page.getMediaBox();
            float centeredXPosition = (pageSize.getWidth() - fontSize / 1000f) / 2f;
            float stringWidth = font.getStringWidth(message);
            float centeredYPosition = (pageSize.getHeight() - (stringWidth * fontSize) / 1000f) / 3f;

            PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false);
            contentStream.setFont(font, fontSize);
            contentStream.beginText();

            for (int i = 0; i < 8; i++) {
                contentStream.setTextMatrix(Matrix.getRotateInstance(i * Math.PI * 0.25,
                        centeredXPosition, pageSize.getHeight() - centeredYPosition));
                contentStream.showText(message + " " + i);
            }

            for (int i = 0; i < 8; i++) {
                contentStream.setTextMatrix(Matrix.getRotateInstance(-i * Math.PI * 0.25,
                        centeredXPosition, centeredYPosition));
                contentStream.showText(message + " " + i);
            }

            contentStream.endText();
            contentStream.close();

            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            fontSize = 1.0f;

            contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false);
            contentStream.setFont(font, fontSize);
            contentStream.beginText();

            for (int i = 0; i < 10; i++) {
                contentStream.setTextMatrix(new Matrix(12f + (i * 6), 0, 0, 12f + (i * 6),
                        100, 100f + i * 50));
                contentStream.showText(message + " " + i);
            }

            contentStream.endText();
            contentStream.close();

            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            fontSize = 1.0f;

            contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false);
            contentStream.setFont(font, fontSize);
            contentStream.beginText();

            int i = 0;
            contentStream.setTextMatrix(new Matrix(12, 0, 0, 12, centeredXPosition, centeredYPosition * 1.5f));
            contentStream.showText(message + " " + i++);

            contentStream.setTextMatrix(new Matrix(0, 18, -18, 0, centeredXPosition, centeredYPosition * 1.5f));
            contentStream.showText(message + " " + i++);

            contentStream.setTextMatrix(new Matrix(-24, 0, 0, -24, centeredXPosition, centeredYPosition * 1.5f));
            contentStream.showText(message + " " + i++);

            contentStream.setTextMatrix(new Matrix(0, -30, 30, 0, centeredXPosition, centeredYPosition * 1.5f));
            contentStream.showText(message + " " + i++);

            contentStream.endText();
            contentStream.close();
            doc.save(outfile);
        }
    }

    public static void main(String[] args) throws IOException {
        UsingTextMatrix app = new UsingTextMatrix();
        if (args.length != 2) {
            app.usage();
        } else {
            app.doIt(args[0], args[1]);
        }
    }

    private void usage() {
        System.err.println("usage: " + this.getClass().getName() + " <Message> <output-file>");
    }
}
