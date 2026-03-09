package study.ywork.doc.itext.form;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.element.ILeafElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;
import study.ywork.doc.util.FileUtils;

import java.io.IOException;

public class Buttons {
    private static final String[] RESULT = {
        "buttons.pdf",
        "buttons_filled.pdf"
    };
    private static final String DEST = RESULT[0];
    private static final String RESOURCE = "src/main/resources/js/buttons.js";
    private static final String IMAGE = "src/main/resources/images/info.png";
    private static final String[] LANGUAGES = {"English", "German", "French", "Spanish", "Dutch"};
    private static final String LANGUAGE = "language";

    public static void main(String[] args) {
        new Buttons().manipulatePdf();
    }

    protected void manipulatePdf() {
        createPdf(DEST);
        fillPdf(DEST, RESULT[1]);
    }

    public void createPdf(String filename) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
             Document doc = new Document(pdfDoc)) {
            pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(FileUtils.readFileToString(RESOURCE).replace("\r\n", "\n")));
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfButtonFormField radioGroup = PdfFormField.createRadioGroup(pdfDoc, LANGUAGE, "");

            for (int i = 0; i < LANGUAGES.length; i++) {
                Rectangle rect = new Rectangle(40, 806 - i * 40, 60 - 40, 806 - 788);
                PdfFormField radio = PdfFormField.createRadioButton(pdfDoc, rect, radioGroup, LANGUAGES[i]);
                radio.setBorderColor(ColorConstants.DARK_GRAY);
                radio.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                radio.setCheckType(PdfFormField.TYPE_CIRCLE);
                canvas
                    .beginText()
                    .setFontAndSize(font, 18)
                    .moveText(70, 790 - i * 40 + 20)
                    .showText(LANGUAGES[i])
                    .endText();
            }

            PdfAcroForm.getAcroForm(pdfDoc, true).addField(radioGroup);
            for (int i = 0; i < LANGUAGES.length; i++) {
                PdfFormXObject xObjectApp1 = new PdfFormXObject(new Rectangle(0, 0, 200 - 180, 806 - 788));
                PdfCanvas canvasApp1 = new PdfCanvas(xObjectApp1, pdfDoc);
                canvasApp1
                    .saveState()
                    .rectangle(1, 1, 18, 18)
                    .stroke()
                    .restoreState();
                PdfFormXObject xObjectApp2 = new PdfFormXObject(new Rectangle(0, 0, 200 - 180, 806 - 788));
                PdfCanvas canvasApp2 = new PdfCanvas(xObjectApp2, pdfDoc);
                canvasApp2
                    .saveState()
                    .setFillColorRgb(255, 128, 128)
                    .rectangle(1, 1, 18, 18)
                    .rectangle(180, 806 - i * 40, 200 - 180, 806 - 788)
                    .fillStroke()
                    .moveTo(1, 1)
                    .lineTo(19, 19)
                    .moveTo(1, 19)
                    .lineTo(19, 1)
                    .stroke()
                    .restoreState();

                Rectangle rect = new Rectangle(180, 806 - i * 40, 200 - 180, 806 - 788);
                PdfButtonFormField checkBox = PdfFormField.createCheckBox(pdfDoc, rect, LANGUAGES[i], "Off");
                checkBox.getWidgets().get(0).getNormalAppearanceObject().put(new PdfName("Off"),
                    xObjectApp1.getPdfObject());
                checkBox.getWidgets().get(0).getNormalAppearanceObject().put(new PdfName("Yes"),
                    xObjectApp2.getPdfObject());
                canvas
                    .beginText()
                    .setFontAndSize(font, 18)
                    .moveText(210, 790 - i * 40 + 20)
                    .showText(LANGUAGES[i])
                    .endText();
                PdfAcroForm.getAcroForm(pdfDoc, true).addField(checkBox);
            }

            Rectangle rect = new Rectangle(300, 806, 370 - 300, 806 - 788);
            Button button = new Button("Buttons", "Push me", pdfDoc, rect);
            button.setImage(ImageDataFactory.create(IMAGE));
            button.setButtonBackgroundColor(new DeviceGray(0.75f));
            button.setBorderColor(ColorConstants.DARK_GRAY);
            button.setFontSize(12);

            doc.add(new Paragraph().add(button));

            PdfWidgetAnnotation ann = button.getFormField().getWidgets().get(0);
            ann.setAction(PdfAction.createJavaScript("this.showButtonState()"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void fillPdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            String[] radioStates = form.getField(LANGUAGE).getAppearanceStates();
            form.getField(LANGUAGE).setValue(radioStates[4]);

            for (int i = 0; i < LANGUAGES.length; i++) {
                String[] checkboxStates = form.getField("English").getAppearanceStates();
                form.getField(LANGUAGES[i]).setValue(checkboxStates[i % 2 == 0 ? 1 : 0], false);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private class Button extends AbstractElement<Button> implements ILeafElement, IAccessibleElement {
        DefaultAccessibilityProperties accessibilityProperties;
        private final PdfButtonFormField formField;
        private final String caption;
        private ImageData image;
        private final Rectangle rect;
        private Color borderColor = ColorConstants.BLACK;
        private Color buttonBackgroundColor = ColorConstants.WHITE;

        public Button(String name, String caption, PdfDocument document, Rectangle rect) {
            formField = PdfFormField.createButton(document, new Rectangle(0, 0), 0);
            formField.setFieldName(name);
            formField.setPushButton(true);

            this.caption = caption;
            this.rect = rect;
        }

        @Override
        protected IRenderer makeNewRenderer() {
            return new ButtonRenderer(this);
        }

        @Override
        public DefaultAccessibilityProperties getAccessibilityProperties() {
            if (accessibilityProperties == null) {
                accessibilityProperties = new DefaultAccessibilityProperties(StandardRoles.FIGURE);
            }
            return accessibilityProperties;
        }

        public PdfButtonFormField getFormField() {
            return formField;
        }

        public String getCaption() {
            return caption;
        }

        public void setImage(ImageData image) {
            this.image = image;
        }

        public ImageData getImage() {
            return image;
        }

        public Color getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
        }

        public void setButtonBackgroundColor(Color buttonBackgroundColor) {
            this.buttonBackgroundColor = buttonBackgroundColor;
        }
    }

    class ButtonRenderer extends AbstractRenderer {

        public ButtonRenderer(Button button) {
            super(button);
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            LayoutArea area = layoutContext.getArea().clone();
            Rectangle layoutBox = area.getBBox();
            applyMargins(layoutBox, false);
            Button modelButton = (Button) modelElement;
            occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(modelButton.rect));
            PdfButtonFormField button = ((Button) getModelElement()).getFormField();
            button.getWidgets().get(0).setRectangle(new PdfArray(occupiedArea.getBBox()));
            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
        }

        @Override
        public void draw(DrawContext drawContext) {
            Button modelButton = (Button) modelElement;
            occupiedArea.setBBox(modelButton.rect);

            super.draw(drawContext);
            float width = occupiedArea.getBBox().getWidth();
            float height = occupiedArea.getBBox().getHeight();

            PdfStream str = new PdfStream();
            PdfCanvas canvas = new PdfCanvas(str, new PdfResources(), drawContext.getDocument());
            PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));

            canvas.
                saveState().
                setStrokeColor(modelButton.getBorderColor()).
                setLineWidth(1).
                rectangle(0, 0, occupiedArea.getBBox().getWidth(), occupiedArea.getBBox().getHeight()).
                stroke().
                setFillColor(modelButton.buttonBackgroundColor).
                rectangle(0.5f, 0.5f, occupiedArea.getBBox().getWidth() - 1, occupiedArea.getBBox().getHeight() - 1).
                fill().
                restoreState();

            Paragraph paragraph = new Paragraph(modelButton.getCaption()).setFontSize(10).setMargin(0).setMultipliedLeading(1);
            try (Canvas paraCanvas = new Canvas(canvas, new Rectangle(0, 0, width, height))) {
                paraCanvas.showTextAligned(paragraph, 20, 3, TextAlignment.LEFT, VerticalAlignment.BOTTOM);
            }

            ImageData image = modelButton.getImage();
            if (image != null) {
                PdfImageXObject imageXObject = new PdfImageXObject(image);
                float imageWidth = image.getWidth();

                if (image.getWidth() > modelButton.rect.getWidth() * 2 / 3) {
                    imageWidth = modelButton.rect.getWidth() * 2 / 3;
                }
                if (image.getHeight() > modelButton.rect.getHeight()) {
                    imageWidth = image.getWidth() * (modelButton.rect.getHeight() / image.getHeight()) * 2 / 3;
                }
                Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(imageXObject, 3, 3, imageWidth);
                canvas.addXObjectFittedIntoRectangle(imageXObject, rect);

                xObject.getResources().addImage(imageXObject);
            }

            PdfButtonFormField button = modelButton.getFormField();
            button.getWidgets().get(0).setNormalAppearance(xObject.getPdfObject());
            xObject.getPdfObject().getOutputStream().writeBytes(str.getBytes());
            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(button, drawContext.getDocument().getPage(1));
        }

        @Override
        public IRenderer getNextRenderer() {
            return null;
        }
    }
}
