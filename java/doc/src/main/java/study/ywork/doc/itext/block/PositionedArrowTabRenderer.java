package study.ywork.doc.itext.block;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.TabRenderer;

public class PositionedArrowTabRenderer extends TabRenderer {
    private boolean isLeft;
    private PdfFont font;
    private PageSize pageSize;

    public PositionedArrowTabRenderer(Tab tab, PdfFont font, Document doc, boolean isLeft) {
        super(tab);
        this.isLeft = isLeft;
        this.font = font;
        this.pageSize = new PageSize(doc.getPageEffectiveArea(doc.getPdfDocument().getDefaultPageSize()));
    }

    @Override
    public void draw(DrawContext drawContext) {
        Rectangle rect = getOccupiedAreaBBox().clone().setHeight(20);
        Paragraph p = new Paragraph(new String(new char[]{220}));
        p.setFont(font);

        try (Canvas canvas = new Canvas(drawContext.getCanvas(), rect)) {
            if (isLeft) {
                canvas.showTextAligned(p, pageSize.getLeft() - 15, rect.getBottom() + 5,
                        TextAlignment.CENTER, VerticalAlignment.MIDDLE);
            } else {
                canvas.showTextAligned(p, pageSize.getRight() + 15, rect.getBottom() + 5,
                        drawContext.getDocument().getNumberOfPages(), TextAlignment.CENTER, VerticalAlignment.MIDDLE, (float) Math.PI);
            }
        }
    }
}
