package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationCircle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFreeText;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationHighlight;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLine;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationPolygon;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquare;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

import java.io.IOException;
import java.util.List;

public final class AddAnnotations {
    static final float INCH = 72;

    private AddAnnotations() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: " + AddAnnotations.class.getName() + " <output-pdf>");
            System.exit(1);
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page1 = new PDPage();
            PDPage page2 = new PDPage();
            PDPage page3 = new PDPage();
            document.addPage(page1);
            document.addPage(page2);
            document.addPage(page3);
            List<PDAnnotation> annotations = page1.getAnnotations();

            // Annotations 本身只能被使用一次！
            PDColor red = new PDColor(new float[]{1, 0, 0}, PDDeviceRGB.INSTANCE);
            PDColor blue = new PDColor(new float[]{0, 0, 1}, PDDeviceRGB.INSTANCE);
            PDColor green = new PDColor(new float[]{0, 1, 0}, PDDeviceRGB.INSTANCE);
            PDColor black = new PDColor(new float[]{0, 0, 0}, PDDeviceRGB.INSTANCE);

            PDBorderStyleDictionary borderThick = new PDBorderStyleDictionary();
            borderThick.setWidth(INCH / 12);

            PDBorderStyleDictionary borderThin = new PDBorderStyleDictionary();
            borderThin.setWidth(INCH / 72);

            PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
            borderULine.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
            borderULine.setWidth(INCH / 72);

            float pw = page1.getMediaBox().getUpperRightX();
            float ph = page1.getMediaBox().getUpperRightY();

            PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);
            try (PDPageContentStream contents = new PDPageContentStream(document, page1)) {
                contents.beginText();
                contents.setFont(font, 18);
                contents.newLineAtOffset(INCH, ph - INCH - 18);
                contents.showText("PDFBox");
                contents.newLineAtOffset(0, -(INCH / 2));
                contents.showText("External URL");
                contents.newLineAtOffset(0, -(INCH / 2));
                contents.showText("Jump to page three");
                contents.endText();
            }

            PDAnnotationHighlight txtHighlight = new PDAnnotationHighlight();
            txtHighlight.setColor(new PDColor(new float[]{0, 1, 1}, PDDeviceRGB.INSTANCE));
            txtHighlight.setConstantOpacity((float) 0.2);

            float textWidth = font.getStringWidth("PDFBox") / 1000 * 18;
            PDRectangle position = new PDRectangle();
            position.setLowerLeftX(INCH);
            position.setLowerLeftY(ph - INCH - 18);
            position.setUpperRightX(INCH + textWidth);
            position.setUpperRightY(ph - INCH);
            txtHighlight.setRectangle(position);

            float[] quads = new float[8];
            quads[0] = position.getLowerLeftX();
            quads[1] = position.getUpperRightY() - 2;
            quads[2] = position.getUpperRightX();
            quads[3] = quads[1];
            quads[4] = quads[0];
            quads[5] = position.getLowerLeftY() - 2;
            quads[6] = quads[2];
            quads[7] = quads[5];

            txtHighlight.setQuadPoints(quads);
            txtHighlight.setContents("Highlighted since it's important");
            annotations.add(txtHighlight);

            PDAnnotationLink txtLink = new PDAnnotationLink();
            txtLink.setBorderStyle(borderULine);

            textWidth = font.getStringWidth("External URL") / 1000 * 18;
            position = new PDRectangle();
            position.setLowerLeftX(INCH);
            position.setLowerLeftY(ph - 1.5f * INCH - 20);  // down a couple of points
            position.setUpperRightX(INCH + textWidth);
            position.setUpperRightY(ph - 1.5f * INCH);
            txtLink.setRectangle(position);

            PDActionURI action = new PDActionURI();
            action.setURI("http://pdfbox.apache.org");
            txtLink.setAction(action);
            annotations.add(txtLink);

            PDAnnotationCircle aCircle = new PDAnnotationCircle();
            aCircle.setContents("Circle Annotation");
            aCircle.setInteriorColor(red);
            aCircle.setColor(blue);
            aCircle.setBorderStyle(borderThin);

            position = new PDRectangle();
            position.setLowerLeftX(INCH);
            position.setLowerLeftY(ph - 3 * INCH - INCH);
            position.setUpperRightX(2 * INCH);
            position.setUpperRightY(ph - 3 * INCH);
            aCircle.setRectangle(position);
            annotations.add(aCircle);

            PDAnnotationSquare aSquare = new PDAnnotationSquare();
            aSquare.setContents("Square Annotation");
            aSquare.setColor(red);
            aSquare.setBorderStyle(borderThick);

            position = new PDRectangle();
            position.setLowerLeftX(pw - 2 * INCH);
            position.setLowerLeftY(ph - 3.5f * INCH - INCH);
            position.setUpperRightX(pw - INCH);
            position.setUpperRightY(ph - 3.5f * INCH);
            aSquare.setRectangle(position);
            annotations.add(aSquare);

            PDAnnotationLine aLine = new PDAnnotationLine();
            aLine.setEndPointEndingStyle(PDAnnotationLine.LE_OPEN_ARROW);
            aLine.setContents("Circle->Square");
            aLine.setCaption(true);

            position = new PDRectangle();
            position.setLowerLeftX(2 * INCH);
            position.setLowerLeftY(ph - 3.5f * INCH - INCH);
            position.setUpperRightX(pw - INCH - INCH);
            position.setUpperRightY(ph - 3 * INCH);
            aLine.setRectangle(position);

            float[] linepos = new float[4];
            linepos[0] = 2 * INCH;
            linepos[1] = ph - 3.5f * INCH;
            linepos[2] = pw - 2 * INCH;
            linepos[3] = ph - 4 * INCH;
            aLine.setLine(linepos);

            aLine.setBorderStyle(borderThick);
            aLine.setColor(black);
            annotations.add(aLine);

            PDAnnotationLink pageLink = new PDAnnotationLink();
            pageLink.setBorderStyle(borderULine);

            textWidth = font.getStringWidth("Jump to page three") / 1000 * 18;
            position = new PDRectangle();
            position.setLowerLeftX(INCH);
            position.setLowerLeftY(ph - 2 * INCH - 20);
            position.setUpperRightX(INCH + textWidth);
            position.setUpperRightY(ph - 2 * INCH);
            pageLink.setRectangle(position);

            PDActionGoTo actionGoto = new PDActionGoTo();
            PDPageDestination dest = new PDPageFitWidthDestination();
            dest.setPage(page3);
            actionGoto.setDestination(dest);
            pageLink.setAction(actionGoto);
            annotations.add(pageLink);

            PDAnnotationFreeText freeTextAnnotation = new PDAnnotationFreeText();
            PDColor yellow = new PDColor(new float[]{1, 1, 0}, PDDeviceRGB.INSTANCE);
            freeTextAnnotation.setColor(yellow);
            position = new PDRectangle();
            position.setLowerLeftX(1 * INCH);
            position.setLowerLeftY(ph - 5f * INCH - 3 * INCH);
            position.setUpperRightX(pw - INCH);
            position.setUpperRightY(ph - 5f * INCH);
            freeTextAnnotation.setRectangle(position);
            freeTextAnnotation.setTitlePopup("Sophia Lorem");
            freeTextAnnotation.setSubject("Lorem ipsum");
            freeTextAnnotation.setContents("uppercase Δ, lowercase δ\n"
                    + "Lorem ipsum dolor sit amet, consetetur sadipscing elitr,"
                    + " sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam "
                    + "erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea "
                    + "rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum "
                    + "dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                    + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam "
                    + "erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea "
                    + "rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum "
                    + "dolor sit amet.");
            freeTextAnnotation.setDefaultAppearance("0 0 1 rg /LibSans 20 Tf");
            freeTextAnnotation.setQ(PDVariableText.QUADDING_RIGHT);
            freeTextAnnotation.setIntent(PDAnnotationFreeText.IT_FREE_TEXT_CALLOUT);
            freeTextAnnotation.setCallout(new float[]{0, ph - 9 * INCH, 3 * INCH, ph - 9 * INCH, 4 * INCH, ph - 8 * INCH});
            freeTextAnnotation.setLineEndingStyle(PDAnnotationLine.LE_OPEN_ARROW);
            annotations.add(freeTextAnnotation);

            PDAnnotationPolygon polygon = new PDAnnotationPolygon();
            position = new PDRectangle();
            position.setLowerLeftX(pw - INCH);
            position.setLowerLeftY(ph - INCH);
            position.setUpperRightX(pw - 2 * INCH);
            position.setUpperRightY(ph - 2 * INCH);
            polygon.setRectangle(position);
            polygon.setColor(blue);
            polygon.setInteriorColor(green);
            float[] vertices = {pw - INCH, ph - 2 * INCH,
                    pw - 1.5f * INCH, ph - INCH,
                    pw - 2 * INCH, ph - 2 * INCH};
            polygon.setVertices(vertices);
            polygon.setBorderStyle(borderThick);
            polygon.setContents("Polygon annotation");
            annotations.add(polygon);

            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                acroForm = new PDAcroForm(document);
                document.getDocumentCatalog().setAcroForm(acroForm);
            }

            PDResources dr = acroForm.getDefaultResources();
            if (dr == null) {
                dr = new PDResources();
                acroForm.setDefaultResources(dr);
            }

            dr.put(COSName.HELV, new PDType1Font(FontName.HELVETICA));
            PDType0Font libSansFont = PDType0Font.load(document,
                    AddAnnotations.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"),
                    false);
            dr.put(COSName.getPDFName("LibSans"), libSansFont);

            annotations.forEach(ann -> ann.constructAppearances(document));
            showPageNo(document, page1, "Page 1");
            showPageNo(document, page2, "Page 2");
            showPageNo(document, page3, "Page 3");
            document.save(args[0]);
        }
    }

    private static void showPageNo(PDDocument document, PDPage page, String pageText)
            throws IOException {
        int fontSize = 10;
        try (PDPageContentStream contents =
                     new PDPageContentStream(document, page, PDPageContentStream.AppendMode.PREPEND, true)) {
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            PDFont font = new PDType1Font(FontName.HELVETICA);
            contents.setFont(font, fontSize);
            float textWidth = font.getStringWidth(pageText) / 1000 * fontSize;
            contents.beginText();
            contents.newLineAtOffset(pageWidth / 2 - textWidth / 2, pageHeight - INCH / 2);
            contents.showText(pageText);
            contents.endText();
        }
    }
}
