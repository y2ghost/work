package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AddMessageToEachPage {
    public AddMessageToEachPage() {
        super();
    }

    public void doIt(String file, String message, String outfile) throws IOException {
        try (PDDocument doc = Loader.loadPDF(new File(file))) {
            PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);
            float fontSize = 36.0f;

            for (PDPage page : doc.getPages()) {
                PDRectangle pageSize = page.getMediaBox();
                float stringWidth = font.getStringWidth(message) * fontSize / 1000f;
                // 计算页面中心位置
                int rotation = page.getRotation();
                boolean rotate = rotation == 90 || rotation == 270;
                float pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
                float pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
                float centerX = rotate ? pageHeight / 2f : (pageWidth - stringWidth) / 2f;
                float centerY = rotate ? (pageWidth - stringWidth) / 2f : pageHeight / 2f;

                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true)) {
                    contentStream.beginText();
                    contentStream.setFont(font, fontSize);
                    contentStream.setNonStrokingColor(Color.red);

                    if (rotate) {
                        contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, centerX, centerY));
                    } else {
                        contentStream.setTextMatrix(Matrix.getTranslateInstance(centerX, centerY));
                    }

                    contentStream.showText(message);
                    contentStream.endText();
                }
            }

            doc.save(outfile);
        }
    }

    public static void main(String[] args) throws IOException {
        AddMessageToEachPage app = new AddMessageToEachPage();
        if (args.length != 3) {
            app.usage();
        } else {
            app.doIt(args[0], args[1], args[2]);
        }
    }

    private void usage() {
        System.err.println("usage: " + this.getClass().getName() + " <input-file> <Message> <output-file>");
    }
}
