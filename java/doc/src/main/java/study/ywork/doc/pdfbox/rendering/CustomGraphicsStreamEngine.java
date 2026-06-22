package study.ywork.doc.pdfbox.rendering;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

/**
 * 自定义处理文本和图形
 */
public class CustomGraphicsStreamEngine extends PDFGraphicsStreamEngine {
    protected CustomGraphicsStreamEngine(PDPage page) {
        super(page);
    }

    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/pdfbox/rendering/", "custom-render-demo.pdf");
        try (PDDocument doc = Loader.loadPDF(file)) {
            PDPage page = doc.getPage(0);
            CustomGraphicsStreamEngine engine = new CustomGraphicsStreamEngine(page);
            engine.run();
        }
    }

    public void run() throws IOException {
        processPage(getPage());
        for (PDAnnotation annotation : getPage().getAnnotations()) {
            showAnnotation(annotation);
        }
    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        System.out.printf("appendRectangle %.2f %.2f, %.2f %.2f, %.2f %.2f, %.2f %.2f%n",
                p0.getX(), p0.getY(), p1.getX(), p1.getY(),
                p2.getX(), p2.getY(), p3.getX(), p3.getY());
    }

    @Override
    public void drawImage(PDImage pdImage) {
        System.out.println("drawImage");
    }

    @Override
    public void clip(int windingRule) {
        System.out.println("clip");
    }

    @Override
    public void moveTo(float x, float y) throws IOException {
        System.out.printf("moveTo %.2f %.2f%n", x, y);
    }

    @Override
    public void lineTo(float x, float y) throws IOException {
        System.out.printf("lineTo %.2f %.2f%n", x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        System.out.printf("curveTo %.2f %.2f, %.2f %.2f, %.2f %.2f%n", x1, y1, x2, y2, x3, y3);
    }

    @Override
    public Point2D getCurrentPoint() {
        return new Point2D.Float(0, 0);
    }

    @Override
    public void closePath() {
        System.out.println("closePath");
    }

    @Override
    public void endPath() {
        System.out.println("endPath");
    }

    @Override
    public void strokePath() {
        System.out.println("strokePath");
    }

    @Override
    public void fillPath(int windingRule) {
        System.out.println("fillPath");
    }

    @Override
    public void fillAndStrokePath(int windingRule) {
        System.out.println("fillAndStrokePath");
    }

    @Override
    public void shadingFill(COSName shadingName) {
        System.out.println("shadingFill " + shadingName.toString());
    }

    @Override
    public void showTextString(byte[] string) throws IOException {
        System.out.print("showTextString \"");
        super.showTextString(string);
        System.out.println("\"");
    }

    @Override
    public void showTextStrings(COSArray array) throws IOException {
        System.out.print("showTextStrings \"");
        super.showTextStrings(array);
        System.out.println("\"");
    }

    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement)
            throws IOException {
        System.out.print("showGlyph " + code);
        super.showGlyph(textRenderingMatrix, font, code, displacement);
    }
}
