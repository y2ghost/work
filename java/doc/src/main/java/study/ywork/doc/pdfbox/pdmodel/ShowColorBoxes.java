package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.IOException;

public final class ShowColorBoxes {
    private ShowColorBoxes() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: " + ShowColorBoxes.class.getName() + " <output-file>");
            System.exit(1);
        }

        String filename = args[0];
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.setNonStrokingColor(Color.CYAN);
                contents.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                contents.fill();

                contents.setNonStrokingColor(Color.RED);
                contents.addRect(10, 10, 100, 100);
                contents.fill();

                contents.saveGraphicsState();
                contents.setNonStrokingColor(Color.BLUE);
                contents.transform(Matrix.getRotateInstance(Math.toRadians(105), 200, 500));
                contents.addRect(0, 0, 200, 100);
                contents.fill();
                contents.restoreGraphicsState();
            }

            doc.save(filename);
        }
    }
}
