package study.ywork.doc.itext.pdftool;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConcatenateForms2 {
    private static final String DATASHEET = "src/main/resources/pdfs/datasheet.pdf";
    private static final String DEST = "concatenateForms2.pdf";

    public static void main(String[] args) throws IOException {
        new ConcatenateForms2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            pdfDoc.initializeOutlines();
            PdfPageFormCopier formCopier = new PdfPageFormCopier();

            try (PdfDocument srcDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(renameFieldsIn(DATASHEET, 1))))) {
                srcDoc.copyPagesTo(1, 1, pdfDoc, formCopier);
            }

            try (PdfDocument srcDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(renameFieldsIn(DATASHEET, 2))))) {
                srcDoc.copyPagesTo(1, 1, pdfDoc, formCopier);
            }
        }
    }


    private static byte[] renameFieldsIn(String datasheet, int i) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(datasheet), new PdfWriter(baos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> map = form.getFormFields();
        Set<String> keys = new HashSet<>(map.keySet());

        for (String key : keys) {
            form.renameField(key, String.format("%s_%d", key, i));
        }

        pdfDoc.close();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }
}
