package study.ywork.doc.itext.hello;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StarWarTextPdf {
    private static final String DEST = "star_wars.pdf";

    public static void main(String[] args) throws IOException {
        new StarWarTextPdf().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        List<String> text = new ArrayList<>();
        text.add("            Episode V      ");
        text.add("    THE EMPIRE STRIKES BACK  ");
        text.add("It is a dark time for the");
        text.add("Rebellion. Although the Death");
        text.add("Star has been destroyed,");
        text.add("Imperial troops have driven the");
        text.add("Rebel forces from their hidden");
        text.add("base and pursued them across");
        text.add("the galaxy.");
        text.add("Evading the dreaded Imperial");
        text.add("Starfleet, a group of freedom");
        text.add("fighters led by Luke Skywalker");
        text.add("has established a new secret");
        text.add("base on the remote ice world");
        text.add("of Hoth...");

        int maxStringWidth = 0;
        for (String fragment : text) {
            if (fragment.length() > maxStringWidth) maxStringWidth = fragment.length();
        }

        PageSize ps = PageSize.A4;
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(dest))) {
            PdfPage page = pdf.addNewPage(ps);
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.rectangle(0, 0, ps.getWidth(), ps.getHeight())
                    .setColor(ColorConstants.BLACK, true)
                    .fill();

            canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
            Color yellowColor = new DeviceCmyk(0.f, 0.0537f, 0.769f, 0.051f);
            float lineHeight = 5;
            float yOffset = -40;
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 1)
                    .setColor(yellowColor, true);

            for (int j = 0; j < text.size(); j++) {
                String line = text.get(j);
                float xOffset = ps.getWidth() / 2 - 45 - 8 * j;
                float fontSize = 6f + j;
                float lineSpacing = (lineHeight + j) * j / 1.5f;
                int stringWidth = line.length();
                for (int i = 0; i < stringWidth; i++) {
                    float angle = (maxStringWidth / 2f - i) / 2f;
                    float charXOffset = (4 + (float) j / 2) * i;
                    canvas.setTextMatrix(fontSize, 0,
                                    angle, fontSize / 1.5f,
                                    xOffset + charXOffset, yOffset - lineSpacing)
                            .showText(String.valueOf(line.charAt(i)));
                }
            }

            canvas.endText();
        }
    }
}