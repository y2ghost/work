package study.ywork.doc.itext.inside;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;

public class RemoveLaunchActions {
    private static final String DEST = "removeLaunchActions.pdf";
    private static final String LAUNCH_ACTIONS = "launchAction.pdf";

    public static void main(String[] args) {
        new RemoveLaunchActions().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        changePdf(LAUNCH_ACTIONS, dest);
    }

    public void changePdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            for (int i = 1; i < pdfDoc.getNumberOfPdfObjects(); i++) {
                PdfObject object = pdfDoc.getPdfObject(i);
                if (object instanceof PdfDictionary dictionary) {
                    PdfDictionary action = dictionary.getAsDictionary(PdfName.A);
                    if (action == null) {
                        continue;
                    }

                    if (PdfName.Launch.equals(action.getAsName(PdfName.S))) {
                        action.remove(PdfName.F);
                        action.remove(PdfName.Win);
                        action.put(PdfName.S, PdfName.JavaScript);
                        action.put(PdfName.JS, new PdfString("app.alert('Launch Application Action removed by iText');\r"));
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
