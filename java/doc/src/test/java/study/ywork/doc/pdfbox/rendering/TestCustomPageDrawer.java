package study.ywork.doc.pdfbox.rendering;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class TestCustomPageDrawer {
    @Test
    void testCustomPageDrawer() throws IOException {
        CustomPageDrawer.main(new String[]{});
        BufferedImage bim = ImageIO.read(new File("target", "custom-render.png"));
        Assertions.assertNotNull(bim);
    }
}
