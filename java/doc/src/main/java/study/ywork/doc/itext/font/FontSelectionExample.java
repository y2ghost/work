package study.ywork.doc.itext.font;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.layout.renderer.TextRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontSelectionExample {
    private static final String DEST = "fontSelectionExample.pdf";
    private static final String TEXT
        = """
        These are the protagonists in 'Hero', a movie by Zhang Yimou:
        無名 (Nameless),
        殘劍 (Broken Sword),
        飛雪 (Flying Snow),
        如月 (Moon),
        秦王 (the King),
        長空 (Sky).""";
    private static final String KOZ_MIN_PRO_FONT = "KozMinPro-Regular";
    private static final String TIMES_ROMAN_FONT = StandardFonts.TIMES_ROMAN;

    public static void main(String[] args) {
        new FontSelectionExample().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Paragraph paragraph = new Paragraph(TEXT);
            FontProvider provider = new FontProvider();
            provider.addFont(TIMES_ROMAN_FONT);
            provider.addFont(KOZ_MIN_PRO_FONT, "UniJIS-UCS2-H");

            doc.setFontProvider(provider);
            paragraph.setFontFamily(TIMES_ROMAN_FONT);

            Map<String, Color> fontColorMap = new HashMap<>();
            fontColorMap.put(KOZ_MIN_PRO_FONT, ColorConstants.RED);
            fontColorMap.put(TIMES_ROMAN_FONT, ColorConstants.BLUE);

            paragraph.setNextRenderer(new CustomParagraphRenderer(paragraph, fontColorMap));
            doc.add(paragraph);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static class CustomParagraphRenderer extends ParagraphRenderer {
        private final Map<String, Color> fontColorMap;

        public CustomParagraphRenderer(Paragraph modelElement, Map<String, Color> fontColorMap) {
            super(modelElement);
            this.fontColorMap = new HashMap<>(fontColorMap);
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            LayoutResult result = super.layout(layoutContext);
            List<LineRenderer> lines = getLines();
            updateColor(lines);
            return result;
        }

        private void updateColor(List<LineRenderer> lines) {
            for (LineRenderer renderer : lines) {
                List<IRenderer> children = renderer.getChildRenderers();
                for (IRenderer child : children) {
                    if (child instanceof TextRenderer textRenderer) {
                        PdfFont pdfFont = textRenderer.getPropertyAsFont(Property.FONT);
                        if (null != pdfFont) {
                            Color updatedColor = fontColorMap
                                .get(pdfFont.getFontProgram().getFontNames().getFontName());
                            if (null != updatedColor) {
                                child.setProperty(Property.FONT_COLOR, new TransparentColor(updatedColor));
                            }
                        }
                    }
                }
            }
        }
    }
}
