package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPatternContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;

import java.awt.*;
import java.io.IOException;

public final class CreatePatternsPDF {
    private CreatePatternsPDF() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            page.setResources(new PDResources());

            try (PDPageContentStream pcs = new PDPageContentStream(doc, page)) {
                PDColorSpace patternCS1 = new PDPattern(null, PDDeviceRGB.INSTANCE);

                PDTilingPattern tilingPattern1 = new PDTilingPattern();
                tilingPattern1.setBBox(new PDRectangle(0, 0, 10, 10));
                tilingPattern1.setPaintType(PDTilingPattern.PAINT_COLORED);
                tilingPattern1.setTilingType(PDTilingPattern.TILING_CONSTANT_SPACING);
                tilingPattern1.setXStep(10);
                tilingPattern1.setYStep(10);

                COSName patternName1 = page.getResources().add(tilingPattern1);
                try (PDPatternContentStream cs1 = new PDPatternContentStream(tilingPattern1)) {
                    cs1.setStrokingColor(Color.red);
                    cs1.moveTo(0, 0);
                    cs1.lineTo(10, 10);
                    cs1.moveTo(-1, 9);
                    cs1.lineTo(1, 11);
                    cs1.moveTo(9, -1);
                    cs1.lineTo(11, 1);
                    cs1.stroke();
                }

                PDColor patternColor1 = new PDColor(patternName1, patternCS1);
                pcs.setNonStrokingColor(patternColor1);
                pcs.addRect(50, 500, 200, 200);
                pcs.fill();

                PDTilingPattern tilingPattern2 = new PDTilingPattern();
                tilingPattern2.setBBox(new PDRectangle(0, 0, 10, 10));
                tilingPattern2.setPaintType(PDTilingPattern.PAINT_UNCOLORED);
                tilingPattern2.setTilingType(PDTilingPattern.TILING_NO_DISTORTION);
                tilingPattern2.setXStep(10);
                tilingPattern2.setYStep(10);

                COSName patternName2 = page.getResources().add(tilingPattern2);
                try (PDPatternContentStream cs2 = new PDPatternContentStream(tilingPattern2)) {
                    cs2.moveTo(0, 5);
                    cs2.lineTo(10, 5);
                    cs2.moveTo(5, 0);
                    cs2.lineTo(5, 10);
                    cs2.stroke();
                }

                PDColorSpace patternCS2 = new PDPattern(null, PDDeviceRGB.INSTANCE);
                PDColor patternColor2green = new PDColor(
                        new float[]{0, 1, 0},
                        patternName2,
                        patternCS2);

                pcs.setNonStrokingColor(patternColor2green);
                pcs.addRect(300, 500, 100, 100);
                pcs.fill();

                PDColor patternColor2blue = new PDColor(
                        new float[]{0, 0, 1},
                        patternName2,
                        patternCS2);
                pcs.setNonStrokingColor(patternColor2blue);
                pcs.addRect(455, 505, 100, 100);
                pcs.fill();
            }

            doc.save("patterns.pdf");
        }
    }
}
