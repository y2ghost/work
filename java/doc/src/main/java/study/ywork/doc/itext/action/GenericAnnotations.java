package study.ywork.doc.itext.action;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TextRenderer;

import java.io.IOException;

public class GenericAnnotations {
    private static final String DEST = "genericAnnotations.pdf";
    private static final String[] ICONS = {
            "Comment", "Key", "Note", "Help", "NewParagraph", "Paragraph", "Insert"
    };

    public static void main(String[] args) {
        new GenericAnnotations().manipulatePdf();
    }

    public void manipulatePdf() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
             Document doc = new Document(pdfDoc)) {
            Paragraph p = new Paragraph();
            for (String icon : ICONS) {
                Text text = new Text(icon);
                text.setNextRenderer(new AnnotatedTextRenderer(text));
                p.add(text);
                p.add(new Text("           "));
            }
            doc.add(p);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }


    private static class AnnotatedTextRenderer extends TextRenderer {
        private final String elementText;

        public AnnotatedTextRenderer(Text textElement) {
            super(textElement);
            elementText = textElement.getText();
        }

        @Override
        public IRenderer getNextRenderer() {
            return new AnnotatedTextRenderer((Text) modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            Rectangle rect = getOccupiedAreaBBox();
            PdfAnnotation annotation = new PdfTextAnnotation(
                    new Rectangle(
                            rect.getRight() + 10, rect.getBottom(),
                            rect.getWidth() + 20, rect.getHeight()));
            annotation.setTitle(new PdfString("Text annotation"));
            annotation.put(PdfName.Subtype, PdfName.Text);
            annotation.put(PdfName.Open, PdfBoolean.FALSE);
            annotation.put(PdfName.Contents,
                    new PdfString(String.format("Icon: %s", elementText)));
            annotation.put(PdfName.Name, new PdfName(elementText));
            drawContext.getDocument().getLastPage().addAnnotation(annotation);
        }
    }
}
