package study.ywork.doc.pdfbox.interactive.form;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;

import java.io.File;
import java.io.IOException;

public final class UpdateFieldOnDocumentOpen {
    private UpdateFieldOnDocumentOpen() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File("target/SimpleForm.pdf"))) {
            String javaScript = "var now = util.printd('yyyy-mm-dd', new Date());"
                    + "var oField = this.getField('SampleField');"
                    + "oField.value = now;";
            PDActionJavaScript jsAction = new PDActionJavaScript();
            jsAction.setAction(javaScript);
            document.getDocumentCatalog().setOpenAction(jsAction);
            document.save("target/UpdateFieldOnDocumentOpen.pdf");
        }
    }
}
