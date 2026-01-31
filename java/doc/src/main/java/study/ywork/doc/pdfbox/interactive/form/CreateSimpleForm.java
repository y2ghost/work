package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

import java.io.IOException;

public final class CreateSimpleForm {
    static final String DEFAULT_FILENAME = "target/SimpleForm.pdf";

    private CreateSimpleForm() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDFont font = new PDType1Font(FontName.HELVETICA);
            PDResources resources = new PDResources();
            resources.put(COSName.HELV, font);
            PDAcroForm acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);
            acroForm.setDefaultResources(resources);
            String defaultAppearanceString = "/Helv 0 Tf 0 g";
            acroForm.setDefaultAppearance(defaultAppearanceString);
            PDTextField textBox = new PDTextField(acroForm);
            textBox.setPartialName("SampleField");
            defaultAppearanceString = "/Helv 12 Tf 0 0 1 rg";
            textBox.setDefaultAppearance(defaultAppearanceString);
            acroForm.getFields().add(textBox);
            PDAnnotationWidget widget = textBox.getWidgets().get(0);
            PDRectangle rect = new PDRectangle(50, 750, 200, 50);
            widget.setRectangle(rect);
            widget.setPage(page);
            PDAppearanceCharacteristicsDictionary fieldAppearance
                    = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
            fieldAppearance.setBorderColour(new PDColor(new float[]{0, 1, 0}, PDDeviceRGB.INSTANCE));
            fieldAppearance.setBackground(new PDColor(new float[]{1, 1, 0}, PDDeviceRGB.INSTANCE));
            widget.setAppearanceCharacteristics(fieldAppearance);

            // 确保小部件注释在屏幕和打印都是可见的
            widget.setPrinted(true);
            page.getAnnotations().add(widget);
            textBox.setQ(PDVariableText.QUADDING_CENTERED);
            textBox.setValue("Sample field content");

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(FontName.HELVETICA), 15);
                cs.newLineAtOffset(50, 810);
                cs.showText("Field:");
                cs.endText();
            }

            if (args == null || args.length == 0) {
                document.save(DEFAULT_FILENAME);
            } else {
                document.save(args[0]);
            }
        }
    }
}
