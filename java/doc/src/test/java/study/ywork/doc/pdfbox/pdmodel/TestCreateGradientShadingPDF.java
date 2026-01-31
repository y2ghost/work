package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

class TestCreateGradientShadingPDF {
    @Test
    void testCreateGradientShading() throws IOException {
        String filename = "target/GradientShading.pdf";

        CreateGradientShadingPDF creator = new CreateGradientShadingPDF();
        creator.create(filename);

        try (PDDocument document = Loader.loadPDF(new File(filename))) {
            Set<Color> set = new HashSet<>();
            BufferedImage bim = new PDFRenderer(document).renderImage(0);
            for (int x = 0; x < bim.getWidth(); ++x) {
                for (int y = 0; y < bim.getHeight(); ++y) {
                    set.add(new Color(bim.getRGB(x, y)));
                }
            }
            Assertions.assertTrue(set.size() > 10000);
        }
    }
}
