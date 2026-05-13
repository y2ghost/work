package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;

public final class DetermineTextFitsField {
    private DetermineTextFitsField() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File("target/SimpleForm.pdf"))) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            PDField field = acroForm.getField("SampleField");
            PDAnnotationWidget widget = field.getWidgets().get(0);
            float widthOfField = widget.getRectangle().getWidth();
            String defaultAppearance = ((PDTextField) field).getDefaultAppearance();
            String[] parts = defaultAppearance.split(" ");
            COSName fontName = COSName.getPDFName(parts[0].substring(1));
            float fontSize = Float.parseFloat(parts[1]);

            PDFont font = null;
            PDResources resources = widget.getNormalAppearanceStream().getResources();
            if (resources != null) {
                font = resources.getFont(fontName);
            }
            if (font == null) {
                font = acroForm.getDefaultResources().getFont(fontName);
            }

            String willFit = "short string";
            String willNotFit = "this is a very long string which will not fit the width of the widget";
            float willFitWidth = font.getStringWidth(willFit) * fontSize / 1000;
            float willNotFitWidth = font.getStringWidth(willNotFit) * fontSize / 1000;
            assert willFitWidth < widthOfField;
            assert willNotFitWidth > widthOfField;
        }
    }
}
