package study.ywork.doc.itext.page;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class OptionalContentExample {
    private static final String DEST = "optionalContentExample.pdf";

    public static void main(String[] args) {
        new OptionalContentExample().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfWriter writer = new PdfWriter(dest, new WriterProperties().setPdfVersion(PdfVersion.PDF_1_5));
             PdfDocument pdfDoc = new PdfDocument(writer)) {
            PdfLayer nested = new PdfLayer("Nested layers", pdfDoc);
            PdfLayer nested1 = new PdfLayer("Nested layer 1", pdfDoc);
            PdfLayer nested2 = new PdfLayer("Nested layer 2", pdfDoc);
            nested.addChild(nested1);
            nested.addChild(nested2);
            nested2.setLocked(true);
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage())
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);
            canvas.beginLayer(nested)
                    .beginText()
                    .moveText(50, 750)
                    .showText("nested layers")
                    .endText()
                    .endLayer();
            canvas.beginLayer(nested1)
                    .beginText()
                    .moveText(100, 780)
                    .showText("nested layer 1")
                    .endText()
                    .endLayer();
            canvas.beginLayer(nested2)
                    .beginText()
                    .moveText(100, 720)
                    .showText("nested layer 2")
                    .endText()
                    .endLayer();

            PdfLayer group = PdfLayer.createTitle("Grouped layers", pdfDoc);
            PdfLayer layer1 = new PdfLayer("Group: layer 1", pdfDoc);
            PdfLayer layer2 = new PdfLayer("Group: layer 2", pdfDoc);
            group.addChild(layer1);
            group.addChild(layer2);
            canvas.beginLayer(layer1)
                    .beginText()
                    .moveText(50, 700)
                    .showText("layer 1 in the group")
                    .endText()
                    .endLayer();
            canvas.beginLayer(layer2)
                    .beginText()
                    .moveText(50, 675)
                    .showText("layer 2 in the group")
                    .endText()
                    .endLayer();

            PdfLayer radiogroup = PdfLayer.createTitle("Radio group", pdfDoc);
            PdfLayer radio1 = new PdfLayer("Radiogroup: layer 1", pdfDoc);
            radio1.setOn(true);
            PdfLayer radio2 = new PdfLayer("Radiogroup: layer 2", pdfDoc);
            radio2.setOn(false);
            PdfLayer radio3 = new PdfLayer("Radiogroup: layer 3", pdfDoc);
            radio3.setOn(false);
            radiogroup.addChild(radio1);
            radiogroup.addChild(radio2);
            radiogroup.addChild(radio3);
            List<PdfLayer> options = Arrays.asList(radio1, radio2, radio3);
            PdfLayer.addOCGRadioGroup(pdfDoc, options);
            canvas.beginLayer(radio1)
                    .beginText()
                    .moveText(50, 600)
                    .showText("option 1")
                    .endText()
                    .endLayer();
            canvas.beginLayer(radio2)
                    .beginText()
                    .moveText(50, 575)
                    .showText("option 2")
                    .endText()
                    .endLayer();
            canvas.beginLayer(radio3)
                    .beginText()
                    .moveText(50, 550)
                    .showText("option 3")
                    .endText()
                    .endLayer();

            PdfLayer notPrinted = new PdfLayer("not printed", pdfDoc);
            notPrinted.setOnPanel(false);
            notPrinted.setPrint("Print", false);
            canvas.beginLayer(notPrinted)
                    .beginText()
                    .moveText(300, 700)
                    .showText("PRINT THIS PAGE")
                    .endText()
                    .endLayer();

            PdfLayer zoom = new PdfLayer("Zoom 0.75-1.25", pdfDoc);
            zoom.setOnPanel(false);
            zoom.setZoom(0.75f, 1.25f);
            canvas.beginLayer(zoom)
                    .beginText()
                    .moveText(30, 530)
                    .showText("Only visible if the zoomfactor is between 75 and 125%")
                    .endText()
                    .endLayer()
                    .release();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
