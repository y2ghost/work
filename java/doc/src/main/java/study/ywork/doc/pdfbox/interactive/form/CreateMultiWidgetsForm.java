package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CreateMultiWidgetsForm {
    private CreateMultiWidgetsForm() {
    }

    public static void main(String[] args) throws IOException {
        // 创建两个空白页面的PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page1 = new PDPage(PDRectangle.A4);
            document.addPage(page1);
            PDPage page2 = new PDPage(PDRectangle.A4);
            document.addPage(page2);

            // Adobe使用Helvetica作为默认字体
            // 它使用 /Helv 的名称存储在资源字典中
            PDFont font = new PDType1Font(FontName.HELVETICA);
            PDResources resources = new PDResources();
            resources.put(COSName.HELV, font);

            PDAcroForm acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);
            acroForm.setDefaultResources(resources);

            // Acrobat sets the font size on the form level to be
            // 自适应字体大小设置 fontSize 为 0
            String defaultAppearanceString = "/Helv 0 Tf 0 g";
            acroForm.setDefaultAppearance(defaultAppearanceString);

            PDTextField textBox = new PDTextField(acroForm);
            textBox.setPartialName("SampleField");

            defaultAppearanceString = "/Helv 12 Tf 0 0 1 rg";
            textBox.setDefaultAppearance(defaultAppearanceString);

            acroForm.getFields().add(textBox);
            PDAnnotationWidget widget1 = new PDAnnotationWidget();
            PDRectangle rect = new PDRectangle(50, 750, 250, 50);
            widget1.setRectangle(rect);
            widget1.setPage(page1);
            widget1.setParent(textBox);

            PDAnnotationWidget widget2 = new PDAnnotationWidget();
            PDRectangle rect2 = new PDRectangle(200, 650, 100, 50);
            widget2.setRectangle(rect2);
            widget2.setPage(page2);
            widget2.setParent(textBox);

            // 为第一个小部件设置绿色边框和黄色背景
            PDAppearanceCharacteristicsDictionary fieldAppearance1
                    = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
            fieldAppearance1.setBorderColour(new PDColor(new float[]{0, 1, 0}, PDDeviceRGB.INSTANCE));
            fieldAppearance1.setBackground(new PDColor(new float[]{1, 1, 0}, PDDeviceRGB.INSTANCE));
            widget1.setAppearanceCharacteristics(fieldAppearance1);

            // 为第二个小部件设置红色边框和绿色背景
            PDAppearanceCharacteristicsDictionary fieldAppearance2
                    = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
            fieldAppearance2.setBorderColour(new PDColor(new float[]{1, 0, 0}, PDDeviceRGB.INSTANCE));
            fieldAppearance2.setBackground(new PDColor(new float[]{0, 1, 0}, PDDeviceRGB.INSTANCE));
            widget2.setAppearanceCharacteristics(fieldAppearance2);

            List<PDAnnotationWidget> widgets = new ArrayList<>();
            widgets.add(widget1);
            widgets.add(widget2);
            textBox.setWidgets(widgets);

            widget1.setPrinted(true);
            widget2.setPrinted(true);

            page1.getAnnotations().add(widget1);
            page2.getAnnotations().add(widget2);
            textBox.setValue("Sample field");
            document.save("target/MultiWidgetsForm.pdf");
        }
    }
}
