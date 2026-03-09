package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;

public final class SuperimposePage {
    private SuperimposePage() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("usage: " + SuperimposePage.class.getName() +
                    " <source-pdf> <dest-pdf>");
            System.exit(1);
        }

        String sourcePath = args[0];
        String destPath = args[1];

        try (PDDocument sourceDoc = Loader.loadPDF(new File(sourcePath))) {
            int sourcePage = 1;
            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                    contents.beginText();
                    contents.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 12);
                    contents.newLineAtOffset(2, PDRectangle.LETTER.getHeight() - 12);
                    contents.showText("Sample text");
                    contents.endText();

                    LayerUtility layerUtility = new LayerUtility(doc);
                    PDFormXObject form = layerUtility.importPageAsForm(sourceDoc, sourcePage - 1);
                    contents.drawForm(form);

                    contents.saveGraphicsState();
                    Matrix matrix = Matrix.getScaleInstance(0.5f, 0.5f);
                    contents.transform(matrix);
                    contents.drawForm(form);
                    contents.restoreGraphicsState();

                    contents.saveGraphicsState();
                    matrix.rotate(1.8 * Math.PI);
                    contents.transform(matrix);
                    contents.drawForm(form);
                    contents.restoreGraphicsState();
                }

                doc.save(destPath);
            }
        }
    }
}
