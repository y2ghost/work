package study.ywork.doc.itext.pdftool;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class FormInformation {
    private static final String DATASHEET = "src/main/resources/pdfs/datasheet.pdf";
    private static final String DEST = "formInformation.txt";

    public static void main(String[] args) throws IOException {
        new FormInformation().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(dest));
        PdfReader reader = new PdfReader(DATASHEET);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Set<String> fields = form.getFormFields().keySet();

        for (String key : fields) {
            writer.print(key + ": ");
            PdfName type = form.getField(key).getFormType();
            if (0 == PdfName.Btn.compareTo(type)) {
                writer.println("Button");
            } else if (0 == PdfName.Ch.compareTo(type)) {
                writer.println("Choicebox");
            } else if (0 == PdfName.Sig.compareTo(type)) {
                writer.println("Signature");
            } else if (0 == PdfName.Tx.compareTo(type)) {
                writer.println("Text");
            } else {
                writer.println("?");
            }
        }

        writer.println("Possible values for CP_1:");
        String[] states = form.getField("CP_1").getAppearanceStates();

        for (int i = 0; i < states.length; i++) {
            writer.print(" - ");
            writer.println(states[i]);
        }

        writer.println("Possible values for category:");
        states = form.getField("category").getAppearanceStates();

        for (int i = 0; i < states.length - 1; i++) {
            writer.print(states[i]);
            writer.print(", ");
        }

        writer.println(states[states.length - 1]);
        writer.flush();
        writer.close();
        reader.close();
    }
}
