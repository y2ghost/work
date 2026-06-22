package study.ywork.doc.itext.inside;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import study.ywork.doc.util.FileUtils;

import java.io.IOException;

public class AddJavaScriptToForm {
    private static final String[] RESULT = {
            "addJavaScriptToForm.pdf",
            "addJavaScriptToForm_form_without_js.pdf"
    };
    private static final String DEST = RESULT[0];
    private static final String RESOURCE = "src/main/resources/js/extra.js";

    public static void main(String[] args) {
        new AddJavaScriptToForm().manipulatePdf(DEST);
    }

    private void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12);
            canvas.moveText(36, 770);
            canvas.showText("Married?");
            canvas.moveText(22, -20);
            canvas.showText("YES");
            canvas.moveText(44, 0);
            canvas.showText("NO");
            canvas.moveText(-66, -20);
            canvas.showText("Name partner?");
            canvas.endText();

            PdfButtonFormField married = PdfFormField.createRadioGroup(pdfDoc, "married", "Yes");
            Rectangle rectYes = new Rectangle(40, 744, 16, 22);
            PdfFormField.createRadioButton(pdfDoc, rectYes, married, "Yes");
            Rectangle rectNo = new Rectangle(84, 744, 16, 22);
            PdfFormField.createRadioButton(pdfDoc, rectNo, married, "No");
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            form.addField(married);

            Rectangle rect = new Rectangle(40, 710, 160, 16);
            PdfTextFormField partner = PdfFormField.createText(pdfDoc, rect, "partner", "partner");
            partner.setBorderColor(ColorConstants.DARK_GRAY);
            partner.setBorderWidth(0.5f);
            form.addField(partner);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void changePdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(FileUtils.readFileToString(RESOURCE).replace("\r\n", "\n")));

            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfFormField fd = form.getField("married");

            fd.getWidgets().get(0).setAdditionalAction(PdfName.Fo, PdfAction.createJavaScript("setReadOnly(false);"));

            fd.getWidgets().get(1).setAdditionalAction(PdfName.Fo, PdfAction.createJavaScript("setReadOnly(true);"));
            PdfButtonFormField button = PdfFormField.createPushButton(pdfDoc, new Rectangle(40, 690, 160, 20), "submit", "validate and submit");
            button.setVisibility(PdfFormField.VISIBLE_BUT_DOES_NOT_PRINT);
            button.setAction(PdfAction.createJavaScript("validate();"));
            form.addField(button);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected void manipulatePdf(String dest) {
        createPdf(RESULT[1]);
        changePdf(RESULT[1], dest);
    }
}
