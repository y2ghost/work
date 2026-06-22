package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationRubberStamp;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestRubberStampWithImage {
    @Test
    void test() throws IOException {
        String documentFile = "src/test/resources/pdfbox/pdmodel/document.pdf";
        String stampFile = "src/test/resources/pdfbox/pdmodel/stamp.jpg";
        String outFile = "target/test-output/TestRubberStampWithImage.pdf";

        new File("target/test-output").mkdirs();
        BufferedImage bim1;

        try (PDDocument doc1 = Loader.loadPDF(new File(documentFile))) {
            bim1 = new PDFRenderer(doc1).renderImage(0);
        }

        String[] args = new String[]{documentFile, outFile, stampFile};
        RubberStampWithImage rubberStamp = new RubberStampWithImage();
        rubberStamp.doIt(args);

        try (PDDocument doc2 = Loader.loadPDF(new File(outFile))) {
            BufferedImage bim2 = new PDFRenderer(doc2).renderImage(0);
            assertFalse(compareImages(bim1, bim2));
            PDAnnotationRubberStamp rubberStampAnnotation = (PDAnnotationRubberStamp) doc2.getPage(0).getAnnotations().get(0);
            PDAppearanceDictionary appearance = rubberStampAnnotation.getAppearance();
            PDAppearanceEntry normalAppearance = appearance.getNormalAppearance();
            PDAppearanceStream appearanceStream = normalAppearance.getAppearanceStream();
            PDImageXObject ximage = (PDImageXObject) appearanceStream.getResources().getXObject(COSName.getPDFName("Im1"));
            BufferedImage actualStampImage = ximage.getImage();
            BufferedImage expectedStampImage = ImageIO.read(new File(stampFile));
            assertTrue(compareImages(expectedStampImage, actualStampImage));
        }
    }

    private boolean compareImages(BufferedImage bim1, BufferedImage bim2) {
        if (bim1.getWidth() != bim2.getWidth()) {
            return false;
        }
        if (bim1.getHeight() != bim2.getHeight()) {
            return false;
        }
        for (int x = 0; x < bim1.getWidth(); ++x) {
            for (int y = 0; y < bim1.getHeight(); ++y) {
                if (bim1.getRGB(x, y) != bim2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
