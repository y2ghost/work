package study.ywork.doc.itext.action;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
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

import java.io.IOException;

public class Advertisement {
    private static final String DEST = "advertisement.pdf";
    private static final String RESOURCE = "src/main/resources/pdfs/hero.pdf";
    private static final String IMAGE = "src/main/resources/images/close.png";
    private static final String NESTED_TABLES = "src/main/resources/pdfs/nestedTables.pdf";

    public static void main(String[] args) {
        Advertisement application = new Advertisement();
        application.manipulatePdf();
    }

    public void manipulatePdf() {
        try (PdfReader reader = new PdfReader(NESTED_TABLES);
             PdfReader ad = new PdfReader(RESOURCE);
             PdfDocument adDoc = new PdfDocument(ad);
             PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(DEST));
             Document doc = new Document(pdfDoc)) {
            Rectangle rect = new Rectangle(400, 772, 145, 20);

            CustomButton button = new CustomButton("click", "Close this advertisement", pdfDoc, rect);
            button.setImage(new PdfImageXObject(ImageDataFactory.create(IMAGE)));
            button.setButtonBackgroundColor(ColorConstants.RED);
            button.setBorderColor(ColorConstants.RED);
            button.setFontSize(10);
            doc.add(new Paragraph().add(button));

            PdfWidgetAnnotation menubar = button.getButton().getWidgets().get(0);
            String js = "var f1 = getField('click'); f1.display = display.hidden;"
                + "var f2 = getField('advertisement'); f2.display = display.hidden;";
            menubar.setAction(PdfAction.createJavaScript(js));
            rect = new Rectangle(400, 550, 545 - 400, 222);
            button = new CustomButton("advertisement", "Buy the book iText in Action 2nd edition", pdfDoc, rect);
            button.setButtonBackgroundColor(ColorConstants.WHITE);
            button.setBorderColor(ColorConstants.RED);
            button.setImage(adDoc.getPage(1).copyAsFormXObject(pdfDoc));
            button.setFontSize(8);
            doc.add(new Paragraph().add(button));

            PdfWidgetAnnotation advertisement = button.getButton().getWidgets().get(0);
            advertisement.setAction(PdfAction.createURI("https://www.1t3xt.com/docs/book.php"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private class CustomButton extends AbstractElement<CustomButton> implements ILeafElement, IAccessibleElement {
        protected DefaultAccessibilityProperties accessibilityProperties;
        protected PdfButtonFormField button;
        protected String caption;
        protected PdfXObject image;
        protected Rectangle rect;
        protected Color borderColor = ColorConstants.BLACK;
        protected Color buttonBackgroundColor = ColorConstants.WHITE;
        protected int fontSize = 12;

        public CustomButton(String name, String caption, PdfDocument document, Rectangle rect) {
            button = PdfFormField.createPushButton(document,
                new Rectangle(0, 0), name, name);
            this.caption = caption;
            this.rect = rect;
        }

        @Override
        protected IRenderer makeNewRenderer() {
            return new CustomButtonRenderer(this);
        }

        @Override
        public DefaultAccessibilityProperties getAccessibilityProperties() {
            if (accessibilityProperties == null) {
                accessibilityProperties = new DefaultAccessibilityProperties(StandardRoles.FIGURE);
            }
            return accessibilityProperties;
        }

        public PdfButtonFormField getButton() {
            return button;
        }

        public String getCaption() {
            return caption;
        }

        public void setImage(PdfXObject image) {
            this.image = image;
        }

        public PdfXObject getImage() {
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

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }
    }

    class CustomButtonRenderer extends AbstractRenderer {
        public CustomButtonRenderer(CustomButton button) {
            super(button);
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            LayoutArea area = layoutContext.getArea().clone();
            Rectangle layoutBox = area.getBBox();
            applyMargins(layoutBox, false);
            CustomButton modelButton = (CustomButton) modelElement;
            occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(modelButton.rect));
            PdfButtonFormField button = ((CustomButton) getModelElement()).getButton();
            button.getWidgets().get(0).setRectangle(new PdfArray(occupiedArea.getBBox()));
            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
        }

        @Override
        public void draw(DrawContext drawContext) {
            CustomButton modelButton = (CustomButton) modelElement;
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
                rectangle(.5f, .5f, occupiedArea.getBBox().getWidth() - 1, occupiedArea.getBBox().getHeight() - 1).
                fill().
                restoreState();

            Paragraph paragraph = new Paragraph(modelButton.getCaption()).setFontSize(modelButton.getFontSize()).setMargin(0).setMultipliedLeading(1);
            try (Canvas tempCanvas = new Canvas(canvas, new Rectangle(0, 0, width, height))) {
                tempCanvas.showTextAligned(paragraph, 1, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM);
            }

            PdfXObject obj = modelButton.getImage();
            float imageWidth = obj.getWidth();

            if (obj instanceof PdfImageXObject) {
                if (obj.getWidth() > modelButton.rect.getWidth()) {
                    imageWidth = modelButton.rect.getWidth();
                } else if (obj.getHeight() > modelButton.rect.getHeight()) {
                    imageWidth = imageWidth * (modelButton.rect.getHeight() / obj.getHeight());
                }
            }

            if (obj instanceof PdfFormXObject) {
                canvas.addXObjectWithTransformationMatrix(obj, width / obj.getWidth(), 0, 0, height / obj.getHeight(), .5f, .5f);
            } else {
                Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(obj,
                    0 + occupiedArea.getBBox().getWidth() - imageWidth, 0, imageWidth);
                canvas.addXObjectFittedIntoRectangle(obj, rect);
            }

            PdfButtonFormField button = modelButton.getButton();
            button.getWidgets().get(0).setNormalAppearance(xObject.getPdfObject());
            xObject.getPdfObject().getOutputStream().writeBytes(str.getBytes());

            if (obj instanceof PdfImageXObject imageXObject) {
                xObject.getResources().addImage(imageXObject);
            } else {
                xObject.getResources().addForm((PdfFormXObject) obj);
            }

            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(button, drawContext.getDocument().getPage(1));
        }

        @Override
        public IRenderer getNextRenderer() {
            return null;
        }
    }
}
