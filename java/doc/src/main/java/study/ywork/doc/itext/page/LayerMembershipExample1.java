package study.ywork.doc.itext.page;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.layer.PdfLayerMembership;

import java.io.IOException;

public class LayerMembershipExample1 {
    private static final String DEST = "layerMembershipExample1.pdf";

    public static void main(String[] args) {
        new LayerMembershipExample1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfWriter writer = new PdfWriter(dest, new WriterProperties().setPdfVersion(PdfVersion.PDF_1_5));
             PdfDocument pdfDoc = new PdfDocument(writer)) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage())
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);
            PdfLayer dog = new PdfLayer("layer 1", pdfDoc);
            PdfLayer tiger = new PdfLayer("layer 2", pdfDoc);
            PdfLayer lion = new PdfLayer("layer 3", pdfDoc);
            PdfLayerMembership cat = new PdfLayerMembership(pdfDoc);
            cat.addLayer(tiger);
            cat.addLayer(lion);
            PdfLayerMembership noCat = new PdfLayerMembership(pdfDoc);
            noCat.addLayer(tiger);
            noCat.addLayer(lion);
            noCat.setVisibilityPolicy(PdfName.AllOff);
            canvas.beginLayer(dog)
                    .beginText()
                    .moveText(50, 725)
                    .showText("dog")
                    .endText()
                    .endLayer();
            canvas.beginLayer(tiger)
                    .beginText()
                    .moveText(50, 700)
                    .showText("tiger")
                    .endText()
                    .endLayer();
            canvas.beginLayer(lion)
                    .beginText()
                    .moveText(50, 675)
                    .showText("lion")
                    .endText()
                    .endLayer();
            canvas.beginLayer(cat)
                    .beginText()
                    .moveText(50, 650)
                    .showText("cat")
                    .endText()
                    .endLayer();
            canvas.beginLayer(noCat)
                    .beginText()
                    .moveText(50, 650)
                    .showText("no cat")
                    .endText()
                    .endLayer();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
