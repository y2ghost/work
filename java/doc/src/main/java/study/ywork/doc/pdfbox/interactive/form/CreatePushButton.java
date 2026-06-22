package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class CreatePushButton {
    public static void main(String[] args) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDAcroForm acroForm = new PDAcroForm(doc);
            doc.getDocumentCatalog().setAcroForm(acroForm);
            PDPushButton pushButton = new PDPushButton(acroForm);
            pushButton.setPartialName("push");
            acroForm.getFields().add(pushButton);
            PDAnnotationWidget widget = pushButton.getWidgets().get(0);
            page.getAnnotations().add(widget);
            widget.setRectangle(new PDRectangle(50, 500, 100, 100));
            widget.setPrinted(true);
            widget.setPage(page);
            PDActionJavaScript javascriptAction = new PDActionJavaScript("app.alert(\"button pressed\")");
            PDAnnotationAdditionalActions actions = new PDAnnotationAdditionalActions();
            actions.setU(javascriptAction);
            widget.setActions(actions);
            PDFormXObject form = new PDFormXObject(doc);
            form.setResources(new PDResources());
            form.setBBox(new PDRectangle(100, 100));
            form.setFormType(1);
            PDAppearanceDictionary appearanceDictionary = new PDAppearanceDictionary(new COSDictionary());
            widget.setAppearance(appearanceDictionary);
            PDAppearanceStream appearanceStream = new PDAppearanceStream(form.getCOSObject());
            appearanceDictionary.setNormalAppearance(appearanceStream);
            BufferedImage bim = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            PDImageXObject image = LosslessFactory.createFromImage(doc, bim);
            try (PDAppearanceContentStream cs = new PDAppearanceContentStream(appearanceStream)) {
                cs.drawImage(image, 0, 0);
            }
            doc.save("target/PushButtonSample.pdf");
        }
    }
}
