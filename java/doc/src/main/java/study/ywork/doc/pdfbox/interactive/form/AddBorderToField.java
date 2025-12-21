package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;

public final class AddBorderToField {
    static final String RESULT_FILENAME = "target/AddBorderToField.pdf";

    private AddBorderToField() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File(CreateSimpleForm.DEFAULT_FILENAME))) {
            PDAcroForm form = document.getDocumentCatalog().getAcroForm();
            // 获取field，注意field存在多个widgets
            PDField field = form.getField("SampleField");
            PDAnnotationWidget widget = field.getWidgets().get(0);
            // 定义border
            PDAppearanceCharacteristicsDictionary fieldAppearance =
                    new PDAppearanceCharacteristicsDictionary(new COSDictionary());
            PDColor red = new PDColor(new float[]{1, 0, 0}, PDDeviceRGB.INSTANCE);
            fieldAppearance.setBorderColour(red);
            widget.setAppearanceCharacteristics(fieldAppearance);
            document.save(RESULT_FILENAME);
        }
    }
}
