package study.ywork.doc.itext.inside;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;

public class FixBrokenForm {
    private static final String ORIGINAL = "src/main/resources/pdfs/broken_form.pdf";
    private static final String[] RESULT = {
            "fixBrokenForm_fixed_form.pdf",
            "fixBrokenForm_broken_form.pdf",
            "fixBrokenForm_filled_form.pdf"
    };

    public static void main(String[] args) {
        new FixBrokenForm().manipulatePdf();
    }

    protected void manipulatePdf() {
        changePdf(ORIGINAL, RESULT[0]);
        fillData(ORIGINAL, RESULT[1]);
        fillData(RESULT[0], RESULT[2]);
    }

    private void changePdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfDictionary root = pdfDoc.getCatalog().getPdfObject();
            PdfDictionary form = root.getAsDictionary(PdfName.AcroForm);
            PdfArray fields = form.getAsArray(PdfName.Fields);

            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfDictionary page = pdfDoc.getPage(i).getPdfObject();
                PdfArray pdfArray = page.getAsArray(PdfName.Annots);
                for (int j = 0; j < pdfArray.size(); j++) {
                    fields.add(pdfArray.get(j).getIndirectReference());
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void fillData(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            if (null != form.getField("title")) {
                form.getField("title").setValue("The Misfortunates");
            }

            if (null != form.getField("director")) {
                form.getField("director").setValue("Felix Van Groeningen");
            }

            if (null != form.getField("year")) {
                form.getField("year").setValue("2009");
            }

            if (null != form.getField("duration")) {
                form.getField("duration").setValue("108");
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
