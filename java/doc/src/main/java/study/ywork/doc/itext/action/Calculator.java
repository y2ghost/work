package study.ywork.doc.itext.action;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import study.ywork.doc.util.FileUtils;

import java.io.IOException;

public class Calculator {
    private static final String DEST = "calculator.pdf";
    private static final String RESOURCE = "src/main/resources/js/calculator.js";
    Rectangle[] digits = new Rectangle[10];
    Rectangle plus;
    Rectangle minus;
    Rectangle mult;
    Rectangle div;
    Rectangle equals;
    Rectangle clearEntry;
    Rectangle clear;
    Rectangle result;
    Rectangle move;

    public static void main(String[] args) throws IOException {
        new Calculator().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        initializeRectangles();
        createPdf(DEST);
    }

    public void initializeRectangles() {
        digits[0] = createRectangle(3, 1, 1, 1);
        digits[1] = createRectangle(1, 3, 1, 1);
        digits[2] = createRectangle(3, 3, 1, 1);
        digits[3] = createRectangle(5, 3, 1, 1);
        digits[4] = createRectangle(1, 5, 1, 1);
        digits[5] = createRectangle(3, 5, 1, 1);
        digits[6] = createRectangle(5, 5, 1, 1);
        digits[7] = createRectangle(1, 7, 1, 1);
        digits[8] = createRectangle(3, 7, 1, 1);
        digits[9] = createRectangle(5, 7, 1, 1);
        plus = createRectangle(7, 7, 1, 1);
        minus = createRectangle(9, 7, 1, 1);
        mult = createRectangle(7, 5, 1, 1);
        div = createRectangle(9, 5, 1, 1);
        equals = createRectangle(7, 1, 3, 1);
        clearEntry = createRectangle(7, 9, 1, 1);
        clear = createRectangle(9, 9, 1, 1);
        result = createRectangle(1, 9, 5, 1);
        move = createRectangle(8, 3, 1, 1);
    }

    public void createPdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(360, 360))) {
            pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(FileUtils.readFileToString(RESOURCE).replace("\r\n", "\n")));
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            for (int i = 0; i < 10; i++) {
                addPushButton(pdfDoc, digits[i], String.valueOf(i), "this.augment(" + i + ")", font);
            }

            addPushButton(pdfDoc, plus, "+", "this.register('+')", font);
            addPushButton(pdfDoc, minus, "-", "this.register('-')", font);
            addPushButton(pdfDoc, mult, "x", "this.register('*')", font);
            addPushButton(pdfDoc, div, ":", "this.register('/')", font);
            addPushButton(pdfDoc, equals, "=", "this.calculateResult()", font);

            addPushButton(pdfDoc, clearEntry, "CE", "this.reset(false)", font);
            addPushButton(pdfDoc, clear, "C", "this.reset(true)", font);
            addTextField(pdfDoc, result, "result");
            addTextField(pdfDoc, move, "move");
        }
    }

    public void addTextField(PdfDocument pdfDoc, Rectangle rect, String name) {
        PdfTextFormField field = PdfFormField.createText(pdfDoc, rect, name);
        field.setValue("");
        field.setMultiline(false).setPassword(false).setMaxLen(0);
        field.getWidgets().get(0).setHighlightMode(PdfName.None);
        field.getWidgets().get(0).put(PdfName.Q, new PdfNumber(2));
        field.setFieldFlags(PdfFormField.FF_READ_ONLY);
        field.setBorderWidth(1);
        PdfAcroForm.getAcroForm(pdfDoc, true).addField(field);
    }

    public void addPushButton(PdfDocument pdfDoc, Rectangle rect, String btn, String script, PdfFont font) {
        float w = rect.getWidth();
        float h = rect.getHeight();
        PdfButtonFormField pushButton = PdfFormField.createPushButton(pdfDoc,
            rect, "btn_" + btn, "btn_" + btn);
        pushButton.setFieldName("btn_" + btn);
        pushButton.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_PUSH);
        pushButton.getWidgets().get(0)
            .setNormalAppearance(createAppearance(pdfDoc, btn, ColorConstants.GRAY, w, h, font));
        pushButton.getWidgets().get(0)
            .setRolloverAppearance(createAppearance(pdfDoc, btn, ColorConstants.RED, w, h, font));
        pushButton.getWidgets().get(0)
            .setDownAppearance(createAppearance(pdfDoc, btn, ColorConstants.BLUE, w, h, font));
        pushButton.getWidgets().get(0).setAdditionalAction(PdfName.U, PdfAction.createJavaScript(script));
        pushButton.getWidgets().get(0).setAdditionalAction(PdfName.E,
            PdfAction.createJavaScript("this.showMove('" + btn + "');"));
        pushButton.getWidgets().get(0).setAdditionalAction(new PdfName("X"),
            PdfAction.createJavaScript("this.showMove(' ');"));
        PdfAcroForm.getAcroForm(pdfDoc, true).addField(pushButton);
    }

    public PdfDictionary createAppearance(PdfDocument pdfDoc, String btn, Color color, float w, float h, PdfFont font) {
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(w, h));
        PdfCanvas canvas = new PdfCanvas(xObject, pdfDoc);
        canvas
            .saveState()
            .setFillColor(color)
            .rectangle(2, 2, w - 2, h - 2)
            .fill()
            .restoreState();
        Paragraph p = new Paragraph(btn).setFont(font).setFontSize(h / 2);
        new Canvas(xObject, pdfDoc).showTextAligned(p, w / 2, h / 4, 1,
            TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
        return xObject.getPdfObject();
    }

    public Rectangle createRectangle(int column, int row, int width,
                                     int height) {
        column = column * 36 - 18;
        row = row * 36 - 18;
        return new Rectangle(column, row, width * 36, height * 36);
    }
}
