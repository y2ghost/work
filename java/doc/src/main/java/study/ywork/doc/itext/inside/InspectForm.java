package study.ywork.doc.itext.inside;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;

public class InspectForm {
    private static final String DEST = "inspectForm_fieldflags.txt";
    private static final String SUBSCRIBE = "src/main/resources/pdfs/subscribe.pdf";

    public static void main(String[] args) {
        new InspectForm().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(dest));
             PdfDocument pdfDoc = new PdfDocument(new PdfReader(SUBSCRIBE))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            Map<String, PdfFormField> fields = form.getFormFields();
            for (Iterator<String> iterator = fields.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                out.write(key);
                PdfFormField item = fields.get(key);
                PdfDictionary dict = item.getPdfObject();

                if (null == dict.getAsNumber(PdfName.Ff)) {
                    dict = item.getWidgets().get(0).getPdfObject();
                }

                if (null != dict.getAsNumber(PdfName.Ff)) {
                    int flags = dict.getAsNumber(PdfName.Ff).intValue();
                    if ((flags & PdfFormField.FF_PASSWORD) > 0)
                        out.write(" -> password");
                    if ((flags & PdfFormField.FF_MULTILINE) > 0)
                        out.write(" -> multiline");
                }

                out.write('\n');
            }

            out.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
