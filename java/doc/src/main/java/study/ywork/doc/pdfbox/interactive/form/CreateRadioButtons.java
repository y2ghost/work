package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateRadioButtons {
    private CreateRadioButtons() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDAcroForm acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);
            List<String> options = Arrays.asList("a", "b", "c");
            PDRadioButton radioButton = new PDRadioButton(acroForm);
            radioButton.setPartialName("MyRadioButton");
            radioButton.setExportValues(options);

            PDAppearanceCharacteristicsDictionary appearanceCharacteristics = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
            appearanceCharacteristics.setBorderColour(new PDColor(new float[]{1, 0, 0}, PDDeviceRGB.INSTANCE));
            appearanceCharacteristics.setBackground(new PDColor(new float[]{0, 1, 0.3f}, PDDeviceRGB.INSTANCE));

            List<PDAnnotationWidget> widgets = new ArrayList<>();
            for (int i = 0; i < options.size(); i++) {
                PDAnnotationWidget widget = new PDAnnotationWidget();
                widget.setRectangle(new PDRectangle(30, PDRectangle.A4.getHeight() - 40 - i * 35, 30, 30));
                widget.setPrinted(true);
                widget.setAppearanceCharacteristics(appearanceCharacteristics);
                PDBorderStyleDictionary borderStyleDictionary = new PDBorderStyleDictionary();
                borderStyleDictionary.setWidth(2);
                borderStyleDictionary.setStyle(PDBorderStyleDictionary.STYLE_SOLID);
                widget.setBorderStyle(borderStyleDictionary);
                widget.setPage(page);

                COSDictionary apNDict = new COSDictionary();
                apNDict.setItem(COSName.Off, createAppearanceStream(document, widget, false));
                apNDict.setItem(options.get(i), createAppearanceStream(document, widget, true));

                PDAppearanceDictionary appearance = new PDAppearanceDictionary();
                PDAppearanceEntry appearanceNEntry = new PDAppearanceEntry(apNDict);
                appearance.setNormalAppearance(appearanceNEntry);
                widget.setAppearance(appearance);
                // 不要忘记这个，否则按钮不可见
                widget.setAppearanceState("Off");
                widgets.add(widget);
                page.getAnnotations().add(widget);
            }

            radioButton.setWidgets(widgets);
            acroForm.getFields().add(radioButton);

            PDType1Font helvetica = new PDType1Font(FontName.HELVETICA);
            try (PDPageContentStream contents = new PDPageContentStream(document, page)) {
                for (int i = 0; i < options.size(); i++) {
                    contents.beginText();
                    contents.setFont(helvetica, 15);
                    contents.newLineAtOffset(70, PDRectangle.A4.getHeight() - 30 - i * 35);
                    contents.showText(options.get(i));
                    contents.endText();
                }
            }

            radioButton.setValue("c");
            document.save("target/RadioButtonsSample.pdf");
        }
    }

    private static PDAppearanceStream createAppearanceStream(
            final PDDocument document, PDAnnotationWidget widget, boolean on) throws IOException {
        PDRectangle rect = widget.getRectangle();
        PDAppearanceStream onAP = new PDAppearanceStream(document);
        onAP.setBBox(new PDRectangle(rect.getWidth(), rect.getHeight()));
        try (PDAppearanceContentStream onAPCS = new PDAppearanceContentStream(onAP)) {
            PDAppearanceCharacteristicsDictionary appearanceCharacteristics = widget.getAppearanceCharacteristics();
            PDColor backgroundColor = appearanceCharacteristics.getBackground();
            PDColor borderColor = appearanceCharacteristics.getBorderColour();
            float lineWidth = getLineWidth(widget);
            onAPCS.setBorderLine(lineWidth, widget.getBorderStyle(), widget.getBorder());
            onAPCS.setNonStrokingColor(backgroundColor);
            float radius = Math.min(rect.getWidth() / 2, rect.getHeight() / 2);
            drawCircle(onAPCS, rect.getWidth() / 2, rect.getHeight() / 2, radius);
            onAPCS.fill();
            onAPCS.setStrokingColor(borderColor);
            drawCircle(onAPCS, rect.getWidth() / 2, rect.getHeight() / 2, radius - lineWidth / 2);
            onAPCS.stroke();
            if (on) {
                onAPCS.setNonStrokingColor(0f);
                drawCircle(onAPCS, rect.getWidth() / 2, rect.getHeight() / 2, (radius - lineWidth) / 2);
                onAPCS.fill();
            }
        }
        return onAP;
    }

    static float getLineWidth(PDAnnotationWidget widget) {
        PDBorderStyleDictionary bs = widget.getBorderStyle();
        if (bs != null) {
            return bs.getWidth();
        }
        return 1;
    }

    static void drawCircle(PDAppearanceContentStream cs, float x, float y, float r) throws IOException {
        float magic = r * 0.551784f;
        cs.moveTo(x, y + r);
        cs.curveTo(x + magic, y + r, x + r, y + magic, x + r, y);
        cs.curveTo(x + r, y - magic, x + magic, y - r, x, y - r);
        cs.curveTo(x - magic, y - r, x - r, y - magic, x - r, y);
        cs.curveTo(x - r, y + magic, x - magic, y + r, x, y + r);
        cs.closePath();
    }
}
