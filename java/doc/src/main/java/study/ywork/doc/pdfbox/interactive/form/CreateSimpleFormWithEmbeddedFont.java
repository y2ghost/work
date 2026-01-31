package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.IOException;

public class CreateSimpleFormWithEmbeddedFont {
    private CreateSimpleFormWithEmbeddedFont() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDAcroForm acroForm = new PDAcroForm(doc);
            doc.getDocumentCatalog().setAcroForm(acroForm);
            PDFont formFont = PDType0Font.load(doc, CreateSimpleFormWithEmbeddedFont.class.getResourceAsStream(
                    "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"), false);
            final PDResources resources = new PDResources();
            acroForm.setDefaultResources(resources);
            final String fontName = resources.add(formFont).getName();
            acroForm.setDefaultResources(resources);
            String defaultAppearanceString = "/" + fontName + " 0 Tf 0 g";

            PDTextField textBox = new PDTextField(acroForm);
            textBox.setPartialName("SampleField");
            textBox.setDefaultAppearance(defaultAppearanceString);
            acroForm.getFields().add(textBox);

            PDAnnotationWidget widget = textBox.getWidgets().get(0);
            PDRectangle rect = new PDRectangle(50, 700, 200, 50);
            widget.setRectangle(rect);
            widget.setPage(page);
            page.getAnnotations().add(widget);

            PDAppearanceCharacteristicsDictionary fieldAppearance
                    = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
            fieldAppearance.setBorderColour(new PDColor(new float[]{0, 1, 0}, PDDeviceRGB.INSTANCE));
            fieldAppearance.setBackground(new PDColor(new float[]{1, 1, 0}, PDDeviceRGB.INSTANCE));
            widget.setAppearanceCharacteristics(fieldAppearance);
            textBox.setValue("Sample field İ");

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(FontName.HELVETICA), 15);
                cs.newLineAtOffset(50, 760);
                cs.showText("Field:");
                cs.endText();
            }

            doc.save("target/SimpleFormWithEmbeddedFont.pdf");
        }
    }
}
