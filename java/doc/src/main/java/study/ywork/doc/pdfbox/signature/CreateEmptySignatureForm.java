package study.ywork.doc.pdfbox.signature;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

import java.io.IOException;

public final class CreateEmptySignatureForm {
    private CreateEmptySignatureForm() {
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

            PDSignatureField signatureField = new PDSignatureField(acroForm);
            PDAnnotationWidget widget = signatureField.getWidgets().get(0);
            PDRectangle rect = new PDRectangle(50, 650, 200, 50);
            widget.setRectangle(rect);
            widget.setPage(page);
            widget.setPrinted(true);
            page.getAnnotations().add(widget);
            acroForm.getFields().add(signatureField);
            document.save(args[0]);
        }
    }
}
