package study.ywork.doc.itext.form;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;

public class ChildFieldEvent extends CellRenderer {
    private PdfFormField field;
    private float padding;

    public ChildFieldEvent(PdfFormField field, float padding, Cell modelElement) {
        super(modelElement);
        this.field = field;
        this.padding = padding;
    }

    @Override
    public void draw(DrawContext drawContext) {
        PdfWidgetAnnotation widget = field.getWidgets().get(0);
        Rectangle rect = widget.getRectangle().toRectangle();
        Rectangle bBox = getOccupiedAreaBBox();
        rect.setBbox(bBox.getLeft() + padding, bBox.getBottom() + padding,
            bBox.getRight() - padding, bBox.getTop() - padding);
        widget.setRectangle(new PdfArray(rect));
        widget.setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT);
        super.draw(drawContext);
    }
}
