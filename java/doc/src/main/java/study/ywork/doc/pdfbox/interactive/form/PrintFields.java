package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PrintFields {
    public void printFields(PDDocument pdfDocument) {
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        List<PDField> fields = acroForm.getFields();
        System.out.println(fields.size() + " top-level fields were found on the form");

        for (PDField field : fields) {
            processField(field, "|--", field.getPartialName());
        }
    }

    private void processField(PDField field, String sLevel, String sParent) {
        String partialName = field.getPartialName();
        if (field instanceof PDNonTerminalField nonTerminalField) {
            if (!sParent.equals(field.getPartialName()) && partialName != null) {
                sParent = sParent + "." + partialName;
            }

            System.out.println(sLevel + sParent);
            for (PDField child : nonTerminalField.getChildren()) {
                processField(child, "|  " + sLevel, sParent);
            }
        } else {
            String fieldValue = field.getValueAsString();
            StringBuilder outputString = new StringBuilder(sLevel);
            outputString.append(sParent);

            if (partialName != null) {
                outputString.append(".").append(partialName);
            }

            outputString.append(" = ").append(fieldValue);
            outputString.append(",  type=").append(field.getClass().getName());
            System.out.println(outputString);
        }
    }

    public static void main(String[] args) throws IOException {
        PDDocument pdf = null;
        try {
            if (args.length != 1) {
                usage();
            } else {
                pdf = Loader.loadPDF(new File(args[0]));
                PrintFields exporter = new PrintFields();
                exporter.printFields(pdf);
            }
        } finally {
            if (pdf != null) {
                pdf.close();
            }
        }
    }

    private static void usage() {
        System.err.println("usage: PrintFields <pdf-file>");
    }
}
