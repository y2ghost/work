package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

public final class RemoveFirstPage {
    private RemoveFirstPage() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                if (document.isEncrypted()) {
                    throw new IOException("Encrypted documents are not supported for this example");
                }

                if (document.getNumberOfPages() <= 1) {
                    throw new IOException("Error: A PDF document must have at least one page, " +
                            "cannot remove the last page!");
                }

                document.removePage(0);
                document.save(args[1]);
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + RemoveFirstPage.class.getName() + " <input-pdf> <output-pdf>");
    }
}
