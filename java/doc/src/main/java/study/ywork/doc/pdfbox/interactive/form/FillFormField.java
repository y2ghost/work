package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;

public final class FillFormField {
    private FillFormField() {
    }

    public static void main(String[] args) throws IOException {
        String formTemplate = "src/main/resources/pdfbox/interactive/form/FillFormField.pdf";

        try (PDDocument pdfDocument = Loader.loadPDF(new File(formTemplate))) {
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
                PDTextField field = (PDTextField) acroForm.getField("sampleField");
                field.setValue("Text Entry");
                field = (PDTextField) acroForm.getField("fieldsContainer.nestedSampleField");
                field.setValue("Text Entry");
            }

            pdfDocument.save("target/FillFormField.pdf");
        }
    }
}
