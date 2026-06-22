package study.ywork.doc.itext.position;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public class FoobarFilmFestival {
    private static final String DEST = "foobarFilmFestival.pdf";
    private final PdfFont helvetica;
    private final PdfFont freeSans;

    public FoobarFilmFestival() throws IOException {
        this.helvetica = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        this.freeSans = PdfFontFactory.createFont("src/main/resources/fonts/FreeSans.ttf", PdfEncodings.IDENTITY_H);
    }

    public static void main(String[] args) throws Exception {
        new FoobarFilmFestival().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) throws FileNotFoundException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);

        String foobar = "Foobar Film Festival";
        float helveticaWidth = helvetica.getWidth(foobar, 12);
        Text c = new Text(foobar + ": " + helveticaWidth).setFont(helvetica);
        doc.add(new Paragraph(c));

        float chunkWidth = helvetica.getWidth(foobar + ": " + helveticaWidth);
        doc.add(new Paragraph(String.format(Locale.ENGLISH, "Chunk width: %f", chunkWidth)));
        float freeSansWidth = freeSans.getWidth(foobar, 12);
        c = new Text(foobar + ": " + freeSansWidth).setFont(freeSans);
        doc.add(new Paragraph(c));

        chunkWidth = freeSans.getWidth(foobar + ": " + freeSansWidth);
        doc.add(new Paragraph(String.format(Locale.ENGLISH, "Chunk width: %f", chunkWidth)));
        doc.add(new Paragraph("Ascent Helvetica: "
                + helvetica.getAscent(foobar, 12)));
        doc.add(new Paragraph("Ascent Times: "
                + freeSans.getAscent(foobar, 12)));
        doc.add(new Paragraph("Descent Helvetica: "
                + helvetica.getDescent(foobar, 12)));
        doc.add(new Paragraph("Descent Times: "
                + freeSans.getDescent(foobar, 12)));

        helveticaWidth = getWidthPointKerned(helvetica, foobar, 12);
        c = new Text(foobar + ": " + helveticaWidth).setFont(helvetica);
        doc.add(new Paragraph(c));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage())
                .saveState()
                .setLineWidth(0.05f)
                .moveTo(400, 806)
                .lineTo(400, 626)
                .moveTo(508.7f, 806)
                .lineTo(508.7f, 626)
                .moveTo(280, 788)
                .lineTo(520, 788)
                .moveTo(280, 752)
                .lineTo(520, 752)
                .moveTo(280, 716)
                .lineTo(520, 716)
                .moveTo(280, 680)
                .lineTo(520, 680)
                .moveTo(280, 644)
                .lineTo(520, 644)
                .stroke()
                .restoreState();

        canvas.setFontAndSize(helvetica, 12);
        try (Canvas textCanvas = new Canvas(canvas, pdfDoc.getFirstPage().getPageSize())) {
            textCanvas.showTextAligned(foobar, 400, 788, TextAlignment.LEFT)
                    .showTextAligned(foobar, 400, 752, TextAlignment.RIGHT)
                    .showTextAligned(foobar, 400, 716, TextAlignment.CENTER)
                    .showTextAligned(foobar, 400, 680, TextAlignment.CENTER, (float) Math.toRadians(30))
                    .showTextAligned(foobar, 400, 644, TextAlignment.LEFT);
        }

        canvas.saveState()
                .setLineWidth(0.05f)
                .moveTo(200, 590)
                .lineTo(200, 410)
                .moveTo(400, 590)
                .lineTo(400, 410)
                .moveTo(80, 572)
                .lineTo(520, 572)
                .moveTo(80, 536)
                .lineTo(520, 536)
                .moveTo(80, 500)
                .lineTo(520, 500)
                .moveTo(80, 464)
                .lineTo(520, 464)
                .moveTo(80, 428)
                .lineTo(520, 428)
                .stroke()
                .restoreState();

        Paragraph phrase = new Paragraph(foobar).setFont(freeSans);
        doc.showTextAligned(phrase, 200, 572, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        doc.showTextAligned(phrase, 200, 536, 1, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
        doc.showTextAligned(phrase, 200, 500, 1, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
        doc.showTextAligned(phrase, 200, 464, 1, TextAlignment.CENTER, VerticalAlignment.BOTTOM, (float) Math.toRadians(30));
        doc.showTextAligned(phrase, 200, 428, 1, TextAlignment.CENTER, VerticalAlignment.BOTTOM, (float) Math.toRadians(-30));

        c = new Text(foobar).setFont(freeSans);
        c.setHorizontalScaling(0.5f);
        phrase = new Paragraph(c);
        doc.showTextAligned(phrase, 400, 572, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        c = new Text(foobar).setFont(freeSans);

        c.setSkew(15, 15);
        phrase = new Paragraph(c);
        doc.showTextAligned(phrase, 400, 536, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        doc.showTextAligned(phrase, 400, 536, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        c = new Text(foobar).setFont(freeSans);

        c.setSkew(0, 25);
        phrase = new Paragraph(c);
        doc.showTextAligned(phrase, 400, 500, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        c = new Text(foobar).setFont(freeSans);
        c.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE)
                .setStrokeWidth(0.1f)
                .setStrokeColor(ColorConstants.RED);
        phrase = new Paragraph(c);
        doc.showTextAligned(phrase, 400, 464, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        c = new Text(foobar).setFont(freeSans);
        c.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE).
                setStrokeWidth(1);
        phrase = new Paragraph(c);
        doc.showTextAligned(phrase, 400, 428, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        doc.close();
    }

    public float getWidthPointKerned(PdfFont font, String text, float fontSize) {
        float size = font.getWidth(text) * 0.001f * fontSize;
        if (!font.getFontProgram().hasKernPairs()) {
            return size;
        }

        int len = text.length() - 1;
        int kern = 0;
        char[] c = text.toCharArray();

        for (int k = 0; k < len; ++k) {
            kern += font.getFontProgram().getKerning(c[k], c[k + 1]);
        }

        return size + kern * 0.001f * fontSize;
    }
}
