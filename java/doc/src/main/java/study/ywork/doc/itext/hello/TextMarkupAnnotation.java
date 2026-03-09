package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.IOException;

public class TextMarkupAnnotation {
    private static final String DEST = "text_markup_annotation.pdf";

    public static void main(String[] args) throws IOException {
        new TextMarkupAnnotation().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);

        Paragraph p = new Paragraph("The example of text markup annotation.");
        document.showTextAligned(p, 20, 795, 1, TextAlignment.LEFT,
                VerticalAlignment.MIDDLE, 0);

        PdfAnnotation ann = PdfTextMarkupAnnotation.createHighLight(new Rectangle(105, 790, 64, 10),
                        new float[]{169, 790, 105, 790, 169, 800, 105, 800})
                .setColor(ColorConstants.YELLOW)
                .setTitle(new PdfString("Hello!"))
                .setContents(new PdfString("I'm a popup."))
                .setTitle(new PdfString("iText"))
                .setRectangle(new PdfArray(new float[]{100, 600, 200, 100}));
        pdf.getFirstPage().addAnnotation(ann);
        document.close();
    }
}
