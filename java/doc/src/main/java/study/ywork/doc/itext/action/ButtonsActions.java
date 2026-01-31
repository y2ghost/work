package study.ywork.doc.itext.action;

import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

import java.io.IOException;

public class ButtonsActions {
    private static final String DEST = "buttonsActions.pdf";
    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/movieTemplates.pdf";

    public static void main(String[] args) {
        ButtonsActions application = new ButtonsActions();
        application.manipulatePdf();
    }

    public void manipulatePdf() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(MOVIE_TEMPLATES), new PdfWriter(DEST))) {
            PdfButtonFormField saveAs = PdfFormField.createPushButton(pdfDoc,
                new Rectangle(636, 10, 80, 20), "Save", "Save");
            saveAs.setBorderColor(ColorConstants.BLACK);
            saveAs.setBorderWidth(1);
            saveAs.setColor(ColorConstants.RED);
            PdfAnnotation saveAsButton = saveAs.getWidgets().get(0);
            saveAs.setAction(PdfAction.createJavaScript("app.execMenuItem('SaveAs')"));

            PdfButtonFormField mail = PdfFormField.createPushButton(pdfDoc,
                new Rectangle(736, 10, 80, 20), "Mail", "Mail");
            mail.setBorderColor(ColorConstants.BLACK);
            mail.setBorderWidth(1);
            mail.setColor(ColorConstants.RED);
            PdfWidgetAnnotation mailButton = mail.getWidgets().get(0);
            mailButton.setAction(PdfAction.createJavaScript("app.execMenuItem('AcroSendMail:SendMail')"));

            for (int page = 1; page <= pdfDoc.getNumberOfPages(); page++) {
                pdfDoc.getPage(page).addAnnotation(saveAsButton);
                pdfDoc.getPage(page).addAnnotation(mailButton);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
