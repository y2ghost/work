package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;

import java.io.File;
import java.io.IOException;

public final class AddJavascript {
    private AddJavascript() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                PDActionJavaScript javascript = new PDActionJavaScript(
                        "app.alert( {cMsg: 'PDFBox rocks!', nIcon: 3, nType: 0, cTitle: 'PDFBox Javascript example' } );");
                document.getDocumentCatalog().setOpenAction(javascript);
                if (document.isEncrypted()) {
                    throw new IOException("Encrypted documents are not supported for this example");
                }
                document.save(args[1]);
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + AddJavascript.class.getName() + " <input-pdf> <output-pdf>");
    }
}
