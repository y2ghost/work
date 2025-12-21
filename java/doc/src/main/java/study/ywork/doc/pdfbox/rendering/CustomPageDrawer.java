package study.ywork.doc.pdfbox.rendering;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CustomPageDrawer {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/pdfbox/rendering/",
                "custom-render-demo.pdf");

        try (PDDocument doc = Loader.loadPDF(file)) {
            PDFRenderer renderer = new MyPDFRenderer(doc);
            BufferedImage image = renderer.renderImage(0);
            ImageIO.write(image, "PNG", new File("target", "custom-render.png"));
        }
    }

    private static class MyPDFRenderer extends PDFRenderer {
        MyPDFRenderer(PDDocument document) {
            super(document);
        }

        @Override
        protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
            return new MyPageDrawer(parameters);
        }
    }

    private static class MyPageDrawer extends PageDrawer {
        MyPageDrawer(PageDrawerParameters parameters) throws IOException {
            super(parameters);
        }

        @Override
        protected Paint getPaint(PDColor color) throws IOException {
            // if this is the non-stroking color, find red, ignoring alpha channel
            if (!(color.getColorSpace() instanceof PDPattern) &&
                    getGraphicsState().getNonStrokingColor() == color &&
                    color.toRGB() == (Color.RED.getRGB() & 0x00FFFFFF)) {
                // replace it with blue
                return Color.BLUE;
            }
            return super.getPaint(color);
        }

        @Override
        protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code,
                                 Vector displacement) throws IOException {
            super.showGlyph(textRenderingMatrix, font, code, displacement);
            Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
            AffineTransform at = textRenderingMatrix.createAffineTransform();
            bbox = at.createTransformedShape(bbox);

            Graphics2D graphics = getGraphics();
            Color color = graphics.getColor();
            Stroke stroke = graphics.getStroke();
            Shape clip = graphics.getClip();

            graphics.setClip(graphics.getDeviceConfiguration().getBounds());
            graphics.setColor(Color.RED);
            graphics.setStroke(new BasicStroke(.5f));
            graphics.draw(bbox);

            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.setClip(clip);
        }

        @Override
        public void fillPath(int windingRule) throws IOException {
            Shape bbox = getLinePath().getBounds2D();
            super.fillPath(windingRule);

            Graphics2D graphics = getGraphics();
            Color color = graphics.getColor();
            Stroke stroke = graphics.getStroke();
            Shape clip = graphics.getClip();

            graphics.setClip(graphics.getDeviceConfiguration().getBounds());
            graphics.setColor(Color.GREEN);
            graphics.setStroke(new BasicStroke(.5f));
            graphics.draw(bbox);

            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.setClip(clip);
        }

        @Override
        public void showAnnotation(PDAnnotation annotation) throws IOException {
            saveGraphicsState();
            getGraphicsState().setNonStrokeAlphaConstant(0.35);
            super.showAnnotation(annotation);
            restoreGraphicsState();
        }
    }
}
