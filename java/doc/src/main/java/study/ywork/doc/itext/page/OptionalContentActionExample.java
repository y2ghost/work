package study.ywork.doc.itext.page;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.action.PdfActionOcgState;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.util.ArrayList;

public class OptionalContentActionExample {
    private static final String DEST = "optionalContentActionExample.pdf";

    public static void main(String[] args) {
        new OptionalContentActionExample().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfLayer a1 = new PdfLayer("answer 1", pdfDoc);
            PdfLayer a2 = new PdfLayer("answer 2", pdfDoc);
            PdfLayer a3 = new PdfLayer("answer 3", pdfDoc);
            a1.setOn(false);
            a2.setOn(false);
            a3.setOn(false);

            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage())
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);
            canvas.beginText().moveText(50, 766).showText("Q1: Who is the director of the movie 'Paths of Glory'?").endText();
            canvas.beginText().moveText(50, 718).showText("Q2: Who directed the movie 'Lawrence of Arabia'?").endText();
            canvas.beginText().moveText(50, 670).showText("Q3: Who is the director of 'House of Flying Daggers'?").endText();
            canvas.saveState();
            canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);
            canvas.setFillColor(new DeviceRgb(0xFF, 0x00, 0x00));
            canvas.beginLayer(a1).beginText().moveText(50, 742).showText("A1: Stanley Kubrick").endText().endLayer();
            canvas.beginLayer(a2).beginText().moveText(50, 694).showText("A2: David Lean").endText().endLayer();
            canvas.beginLayer(a3).beginText().moveText(50, 646).showText("A3: Zhang Yimou").endText().endLayer();
            canvas.restoreState();
            ArrayList<PdfDictionary> dictList;

            ArrayList<PdfActionOcgState> stateOn = new ArrayList<>();
            dictList = new ArrayList<>();
            dictList.add(a1.getPdfObject());
            stateOn.add(new PdfActionOcgState(PdfName.ON, dictList));
            dictList = new ArrayList<>();
            dictList.add(a2.getPdfObject());
            stateOn.add(new PdfActionOcgState(PdfName.ON, dictList));
            dictList = new ArrayList<>();
            dictList.add(a3.getPdfObject());
            stateOn.add(new PdfActionOcgState(PdfName.ON, dictList));
            PdfAction actionOn = PdfAction.createSetOcgState(stateOn, true);

            ArrayList<PdfActionOcgState> stateOff = new ArrayList<>();
            dictList = new ArrayList<>();
            dictList.add(a1.getPdfObject());
            stateOff.add(new PdfActionOcgState(PdfName.OFF, dictList));
            dictList = new ArrayList<>();
            dictList.add(a2.getPdfObject());
            stateOff.add(new PdfActionOcgState(PdfName.OFF, dictList));
            dictList = new ArrayList<>();
            dictList.add(a3.getPdfObject());
            stateOff.add(new PdfActionOcgState(PdfName.OFF, dictList));
            PdfAction actionOff = PdfAction.createSetOcgState(stateOff, true);

            dictList = new ArrayList<>();
            dictList.add(a1.getPdfObject());
            dictList.add(a2.getPdfObject());
            dictList.add(a3.getPdfObject());
            ArrayList<PdfActionOcgState> stateToggle = new ArrayList<>();
            stateToggle.add(new PdfActionOcgState(new PdfName("Toggle"), dictList));
            PdfAction actionToggle = PdfAction.createSetOcgState(stateToggle, true);

            Paragraph p = new Paragraph("Change the state of the answers:");
            Link on = new Link(" on ", actionOn);
            p.add(on);
            Link off = new Link("/ off ", actionOff);
            p.add(off);
            Link toggle = new Link("/ toggle", actionToggle);
            p.add(toggle);
            doc.add(p);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
