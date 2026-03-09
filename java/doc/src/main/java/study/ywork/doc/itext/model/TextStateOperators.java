package study.ywork.doc.itext.model;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfTextArray;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.io.IOException;

public class TextStateOperators {
    private static final String DEST = "textStateOperators.pdf";

    public static void main(String[] args) {
        new TextStateOperators().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            String text = "AWAY again";
            canvas.beginText().
                    setFontAndSize(font, 16).
                    moveText(36, 806).
                    moveTextWithLeading(0, -24).
                    showText(text).
                    setWordSpacing(20).
                    newlineShowText(text).
                    setCharacterSpacing(10).
                    newlineShowText(text).
                    setWordSpacing(0).
                    setCharacterSpacing(0).
                    setHorizontalScaling(50).
                    newlineShowText(text).
                    setHorizontalScaling(100).
                    newlineShowText(text).
                    setTextRise(15).
                    setFontAndSize(font, 12).
                    setFillColor(ColorConstants.RED).
                    showText("2").
                    setFillColor(DeviceGray.BLACK).
                    setLeading(56).
                    newlineShowText("Changing the leading: " + text).
                    setLeading(24).
                    newlineText();

            PdfTextArray textArray = new PdfTextArray();
            textArray.add(font.convertToBytes("A"));
            textArray.add(120);
            textArray.add(font.convertToBytes("W"));
            textArray.add(120);
            textArray.add(font.convertToBytes("A"));
            textArray.add(95);
            textArray.add(font.convertToBytes("Y again"));

            canvas.showText(textArray).
                    endText();

            canvas.setFillColor(ColorConstants.BLUE).
                    beginText().
                    setTextMatrix(360, 770).
                    setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL).
                    setFontAndSize(font, 24).
                    showText(text).
                    endText().

                    beginText().
                    setTextMatrix(360, 730).
                    setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE).
                    setFontAndSize(font, 24).
                    showText(text).
                    endText().

                    beginText().
                    setTextMatrix(360, 690).
                    setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE).
                    setFontAndSize(font, 24).
                    showText(text).
                    endText().

                    beginText().
                    setTextMatrix(360, 650).
                    setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.INVISIBLE).
                    setFontAndSize(font, 24).
                    showText(text).
                    endText();

            PdfFormXObject xObject = new PdfFormXObject(new Rectangle(200, 36));
            PdfCanvas xObjectCanvas = new PdfCanvas(xObject, pdfDoc).setLineWidth(2);

            for (int i = 0; i < 6; i++) {
                xObjectCanvas.moveTo(0, i * 6F);
                xObjectCanvas.lineTo(200, i * 6F);
            }

            xObjectCanvas.stroke();
            canvas.saveState().
                    beginText().
                    setTextMatrix(360, 610).
                    setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_CLIP).
                    setFontAndSize(font, 24).
                    showText(text).
                    endText().
                    addXObjectAt(xObject, 360, 610).
                    restoreState().

                    saveState().
                    beginText().
                    setTextMatrix(360, 570).
                    setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE_CLIP).
                    setFontAndSize(font, 24).
                    showText(text).
                    endText().
                    addXObjectAt(xObject, 360, 570).
                    restoreState().

                    saveState().
                    beginText().
                    setTextMatrix(360, 530).
                    setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE_CLIP).
                    setFontAndSize(font, 24).
                    showText(text).
                    endText().
                    addXObjectAt(xObject, 360, 530).
                    restoreState().

                    saveState().
                    beginText().
                    setTextMatrix(360, 490).
                    setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.CLIP).
                    setFontAndSize(font, 24).
                    showText(text).
                    endText().
                    addXObjectAt(xObject, 360, 490).
                    restoreState();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
