package study.ywork.doc.pdfbox.interactive.form;

import org.apache.fontbox.afm.CharMetric;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class CreateCheckBox {
    private CreateCheckBox() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDAcroForm form = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(form);
            // Adobe支持调试
            form.setNeedAppearances(true);

            float x = 50;
            float y = page.getMediaBox().getHeight() - 50;

            PDRectangle rect = new PDRectangle(x, y, 20, 20);
            PDCheckBox checkbox = new PDCheckBox(form);
            checkbox.setPartialName("MyCheckBox");
            PDAnnotationWidget widget = checkbox.getWidgets().get(0);
            widget.setPage(page);
            widget.setRectangle(rect);
            widget.setPrinted(true);

            PDAppearanceCharacteristicsDictionary appearanceCharacteristics = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
            appearanceCharacteristics.setBorderColour(new PDColor(new float[]{1, 0, 0}, PDDeviceRGB.INSTANCE));
            appearanceCharacteristics.setBackground(new PDColor(new float[]{1, 1, 0}, PDDeviceRGB.INSTANCE));
            appearanceCharacteristics.setNormalCaption("4");
            widget.setAppearanceCharacteristics(appearanceCharacteristics);

            PDBorderStyleDictionary borderStyleDictionary = new PDBorderStyleDictionary();
            borderStyleDictionary.setWidth(1);
            borderStyleDictionary.setStyle(PDBorderStyleDictionary.STYLE_SOLID);
            widget.setBorderStyle(borderStyleDictionary);

            PDAppearanceDictionary ap = new PDAppearanceDictionary();
            widget.setAppearance(ap);
            PDAppearanceEntry normalAppearance = ap.getNormalAppearance();

            COSDictionary normalAppearanceDict = normalAppearance.getCOSObject();
            PDFont zapfDingbats = new PDType1Font(FontName.ZAPF_DINGBATS);
            normalAppearanceDict.setItem(COSName.Off, createAppearanceStream(document, widget, false, zapfDingbats));
            normalAppearanceDict.setItem(COSName.YES, createAppearanceStream(document, widget, true, zapfDingbats));
            // 实现 a/D(down) 外观，建议背景颜色中c = c * 0.75
            page.getAnnotations().add(checkbox.getWidgets().get(0));
            form.getFields().add(checkbox);
            // 始终显示调用，确保未选中
            checkbox.unCheck();
            document.save("target/CheckBoxSample.pdf");
        }
    }

    private static PDAppearanceStream createAppearanceStream(
            final PDDocument document, PDAnnotationWidget widget, boolean on, PDFont font) throws IOException {
        PDRectangle rect = widget.getRectangle();
        PDAppearanceCharacteristicsDictionary appearanceCharacteristics;
        PDAppearanceStream yesAP = new PDAppearanceStream(document);
        yesAP.setBBox(new PDRectangle(rect.getWidth(), rect.getHeight()));
        yesAP.setResources(new PDResources());
        try (PDAppearanceContentStream yesAPCS = new PDAppearanceContentStream(yesAP)) {
            appearanceCharacteristics = widget.getAppearanceCharacteristics();
            PDColor backgroundColor = appearanceCharacteristics.getBackground();
            PDColor borderColor = appearanceCharacteristics.getBorderColour();
            float lineWidth = getLineWidth(widget);
            yesAPCS.setBorderLine(lineWidth, widget.getBorderStyle(), widget.getBorder());
            yesAPCS.setNonStrokingColor(backgroundColor);
            yesAPCS.addRect(0, 0, rect.getWidth(), rect.getHeight());
            yesAPCS.fill();
            yesAPCS.setStrokingColor(borderColor);
            yesAPCS.addRect(lineWidth / 2, lineWidth / 2, rect.getWidth() - lineWidth, rect.getHeight() - lineWidth);
            yesAPCS.stroke();

            if (!on) {
                return yesAP;
            }

            yesAPCS.addRect(lineWidth, lineWidth, rect.getWidth() - lineWidth * 2, rect.getHeight() - lineWidth * 2);
            yesAPCS.clip();

            String normalCaption = appearanceCharacteristics.getNormalCaption();
            if (normalCaption == null) {
                normalCaption = "4";
            }

            if ("8".equals(normalCaption)) {
                yesAPCS.setStrokingColor(0f);
                yesAPCS.moveTo(lineWidth * 2, rect.getHeight() - lineWidth * 2);
                yesAPCS.lineTo(rect.getWidth() - lineWidth * 2, lineWidth * 2);
                yesAPCS.moveTo(rect.getWidth() - lineWidth * 2, rect.getHeight() - lineWidth * 2);
                yesAPCS.lineTo(lineWidth * 2, lineWidth * 2);
                yesAPCS.stroke();
            } else {
                Rectangle2D bounds = new Rectangle2D.Float();
                String unicode = null;
                // ZapfDingbats可能丢失，使用 AFM 替代
                FontMetrics metric = Standard14Fonts.getAFM(FontName.ZAPF_DINGBATS.getName());

                for (CharMetric cm : metric.getCharMetrics()) {
                    // The caption is not unicode, but the Zapf Dingbats code in the PDF.
                    // Assume that only the first character is used.
                    if (normalCaption.codePointAt(0) == cm.getCharacterCode()) {
                        BoundingBox bb = cm.getBoundingBox();
                        bounds = new Rectangle2D.Float(bb.getLowerLeftX(), bb.getLowerLeftY(),
                                bb.getWidth(), bb.getHeight());
                        unicode = GlyphList.getZapfDingbats().toUnicode(cm.getName());
                        break;
                    }
                }

                if (bounds.isEmpty()) {
                    throw new IOException("Bounds rectangle for chosen glyph is empty");
                }

                float size = (float) Math.min(bounds.getWidth(), bounds.getHeight()) / 1000;
                // 近似adobe的做法，将字形尽量放到正方形中间
                float fontSize = (rect.getWidth() - lineWidth * 2) / size * 0.6666f;
                float xOffset = (float) (rect.getWidth() - (bounds.getWidth()) / 1000 * fontSize) / 2;
                xOffset -= bounds.getX() / 1000 * fontSize;
                float yOffset = (float) (rect.getHeight() - (bounds.getHeight()) / 1000 * fontSize) / 2;
                yOffset -= bounds.getY() / 1000 * fontSize;
                yesAPCS.setNonStrokingColor(0f);
                yesAPCS.beginText();
                yesAPCS.setFont(font, fontSize);
                yesAPCS.newLineAtOffset(xOffset, yOffset);
                yesAPCS.showText(unicode);
                yesAPCS.endText();
            }
        }
        return yesAP;
    }

    static float getLineWidth(PDAnnotationWidget widget) {
        PDBorderStyleDictionary bs = widget.getBorderStyle();
        if (bs != null) {
            return bs.getWidth();
        }
        return 1;
    }
}
