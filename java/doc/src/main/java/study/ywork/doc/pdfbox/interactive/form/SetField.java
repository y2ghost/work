package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDComboBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDListBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;

public class SetField {
    public void setField(PDDocument pdfDocument, String name, String value) throws IOException {
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        PDField field = acroForm.getField(name);

        if (field != null) {
            if (field instanceof PDCheckBox checkBox) {
                if (value.isEmpty()) {
                    checkBox.unCheck();
                } else {
                    checkBox.check();
                }
            } else if (field instanceof PDComboBox) {
                field.setValue(value);
            } else if (field instanceof PDListBox) {
                field.setValue(value);
            } else if (field instanceof PDRadioButton) {
                field.setValue(value);
            } else if (field instanceof PDTextField) {
                field.setValue(value);
            }
        } else {
            System.err.println("No field found with name:" + name);
        }
    }

    public static void main(String[] args) throws IOException {
        SetField setter = new SetField();
        setter.setField(args);
    }

    private void setField(String[] args) throws IOException {
        PDDocument pdf = null;
        try {
            if (args.length != 3) {
                usage();
            } else {
                SetField example = new SetField();
                pdf = Loader.loadPDF(new File(args[0]));
                example.setField(pdf, args[1], args[2]);
                pdf.save(calculateOutputFilename(args[0]));
            }
        } finally {
            if (pdf != null) {
                pdf.close();
            }
        }
    }

    private static String calculateOutputFilename(String filename) {
        String outputFilename;
        if (filename.toLowerCase().endsWith(".pdf")) {
            outputFilename = filename.substring(0, filename.length() - 4);
        } else {
            outputFilename = filename;
        }
        outputFilename += "_filled.pdf";
        return outputFilename;
    }

    private static void usage() {
        System.err.println("usage: SetField <pdf-file> <field-name> <field-value>");
    }
}
