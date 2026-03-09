package study.ywork.doc.pdfbox.util;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType3CharProc;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.font.PDVectorFont;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDThreadBead;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DrawPrintTextLocations extends PDFTextStripper {
    private AffineTransform flipAT;
    private AffineTransform rotateAT;
    private AffineTransform transAT;
    private final String filename;
    static final int SCALE = 4;
    private Graphics2D g2d;

    public DrawPrintTextLocations(PDDocument document, String filename) {
        this.document = document;
        this.filename = filename;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                DrawPrintTextLocations stripper = new DrawPrintTextLocations(document, args[0]);
                stripper.setSortByPosition(true);

                for (int page = 0; page < document.getNumberOfPages(); ++page) {
                    stripper.stripPage(page);
                }
            }
        }
    }

    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement)
            throws IOException {
        super.showGlyph(textRenderingMatrix, font, code, displacement);
        Shape cyanShape = calculateGlyphBounds(textRenderingMatrix, font, code);

        if (cyanShape != null) {
            cyanShape = flipAT.createTransformedShape(cyanShape);
            cyanShape = rotateAT.createTransformedShape(cyanShape);
            cyanShape = transAT.createTransformedShape(cyanShape);
            g2d.setColor(Color.CYAN);
            g2d.draw(cyanShape);
        }
    }

    private Shape calculateGlyphBounds(Matrix textRenderingMatrix, PDFont font, int code) throws IOException {
        GeneralPath path = null;
        AffineTransform at = textRenderingMatrix.createAffineTransform();
        at.concatenate(font.getFontMatrix().createAffineTransform());

        if (font instanceof PDType3Font type3Font) {
            // 对于type3字体，很难计算出真正的单个字形边界
            PDType3CharProc charProc = type3Font.getCharProc(code);
            if (charProc != null) {
                BoundingBox fontBBox = type3Font.getBoundingBox();
                PDRectangle glyphBBox = charProc.getGlyphBBox();

                if (glyphBBox != null) {
                    glyphBBox.setLowerLeftX(Math.max(fontBBox.getLowerLeftX(), glyphBBox.getLowerLeftX()));
                    glyphBBox.setLowerLeftY(Math.max(fontBBox.getLowerLeftY(), glyphBBox.getLowerLeftY()));
                    glyphBBox.setUpperRightX(Math.min(fontBBox.getUpperRightX(), glyphBBox.getUpperRightX()));
                    glyphBBox.setUpperRightY(Math.min(fontBBox.getUpperRightY(), glyphBBox.getUpperRightY()));
                    path = glyphBBox.toGeneralPath();
                }
            }
        } else if (font instanceof PDVectorFont vectorFont) {
            path = vectorFont.getPath(code);

            if (font instanceof PDTrueTypeFont ttFont) {
                int unitsPerEm = ttFont.getTrueTypeFont().getHeader().getUnitsPerEm();
                at.scale(1000d / unitsPerEm, 1000d / unitsPerEm);
            }

            if (font instanceof PDType0Font t0font && t0font.getDescendantFont() instanceof PDCIDFontType2 fontType2) {
                int unitsPerEm = fontType2.getTrueTypeFont().getHeader().getUnitsPerEm();
                at.scale(1000d / unitsPerEm, 1000d / unitsPerEm);
            }

        } else {
            System.out.println("Unknown font class: " + font.getClass());
        }

        if (path == null) {
            return null;
        }

        return at.createTransformedShape(path.getBounds2D());
    }

    private void stripPage(int page) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage image = pdfRenderer.renderImage(page, SCALE);
        PDPage pdPage = document.getPage(page);
        PDRectangle cropBox = pdPage.getCropBox();
        flipAT = new AffineTransform();
        flipAT.translate(0, pdPage.getBBox().getHeight());
        flipAT.scale(1, -1);

        rotateAT = new AffineTransform();
        int rotation = pdPage.getRotation();

        switch (rotation) {
            case 0 -> transAT = AffineTransform.getTranslateInstance(-cropBox.getLowerLeftX(), cropBox.getLowerLeftY());
            case 90 -> {
                rotateAT.translate(cropBox.getHeight(), 0);
                transAT = AffineTransform.getTranslateInstance(-cropBox.getLowerLeftY(), -cropBox.getLowerLeftX());
            }
            case 270 -> {
                rotateAT.translate(0, cropBox.getWidth());
                transAT = AffineTransform.getTranslateInstance(cropBox.getLowerLeftY(), cropBox.getLowerLeftX());
            }
            case 180 -> {
                rotateAT.translate(cropBox.getWidth(), cropBox.getHeight());
                transAT = AffineTransform.getTranslateInstance(cropBox.getLowerLeftX(), -cropBox.getLowerLeftY());
            }
            default -> {
                // NOOP
            }
        }

        rotateAT.rotate(Math.toRadians(rotation));
        g2d = image.createGraphics();
        g2d.setStroke(new BasicStroke(0.1f));
        g2d.scale(SCALE, SCALE);

        setStartPage(page + 1);
        setEndPage(page + 1);

        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream(), StandardCharsets.US_ASCII);
        writeText(document, dummy);
        g2d.setStroke(new BasicStroke(0.4f));
        List<PDThreadBead> pageArticles = pdPage.getThreadBeads();

        for (PDThreadBead bead : pageArticles) {
            if (bead == null) {
                continue;
            }

            PDRectangle r = bead.getRectangle();
            Shape s = r.toGeneralPath().createTransformedShape(transAT);
            s = flipAT.createTransformedShape(s);
            s = rotateAT.createTransformedShape(s);
            g2d.setColor(Color.green);
            g2d.draw(s);
        }

        g2d.dispose();
        String imageFilename = filename;
        int pt = imageFilename.lastIndexOf('.');
        imageFilename = imageFilename.substring(0, pt) + "-marked-" + (page + 1) + ".png";
        ImageIO.write(image, "png", new File(imageFilename));
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
            System.out.println("String[" + text.getXDirAdj() + "," + text.getYDirAdj()
                    + " font=" + text.getFont().getName() + ":" + text.getFontSize() + " xscale="
                    + text.getXScale() + " height=" + text.getHeightDir() + " space="
                    + text.getWidthOfSpace() + " width="
                    + text.getWidthDirAdj() + "]" + text.getUnicode());
            AffineTransform at = text.getTextMatrix().createAffineTransform();
            Rectangle2D.Float rect = new Rectangle2D.Float(0, 0,
                    text.getWidthDirAdj() / text.getTextMatrix().getScalingFactorX(),
                    text.getHeightDir() / text.getTextMatrix().getScalingFactorY());
            Shape s = at.createTransformedShape(rect);
            s = flipAT.createTransformedShape(s);
            s = rotateAT.createTransformedShape(s);
            g2d.setColor(Color.red);
            g2d.draw(s);
            PDFont font = text.getFont();
            BoundingBox bbox = font.getBoundingBox();
            float xadvance = font.getWidth(text.getCharacterCodes()[0]);
            rect = new Rectangle2D.Float(0, bbox.getLowerLeftY(), xadvance, bbox.getHeight());

            if (font instanceof PDType3Font) {
                at.concatenate(font.getFontMatrix().createAffineTransform());
            } else {
                at.scale(1 / 1000f, 1 / 1000f);
            }

            s = at.createTransformedShape(rect);
            s = flipAT.createTransformedShape(s);
            s = rotateAT.createTransformedShape(s);
            g2d.setColor(Color.blue);
            g2d.draw(s);
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + DrawPrintTextLocations.class.getName() + " <input-pdf>");
    }
}
