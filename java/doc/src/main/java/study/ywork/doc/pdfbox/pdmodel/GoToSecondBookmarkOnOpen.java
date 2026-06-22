package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import java.io.File;
import java.io.IOException;

public final class GoToSecondBookmarkOnOpen {
    private GoToSecondBookmarkOnOpen() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                if (document.isEncrypted()) {
                    System.err.println("Error: Cannot add bookmark destination to encrypted documents.");
                    System.exit(1);
                }

                if (document.getNumberOfPages() < 2) {
                    throw new IOException("Error: The PDF must have at least 2 pages.");
                }

                PDDocumentOutline bookmarks = document.getDocumentCatalog().getDocumentOutline();
                if (bookmarks == null) {
                    throw new IOException("Error: The PDF does not contain any bookmarks");
                }

                PDOutlineItem item = bookmarks.getFirstChild().getNextSibling();
                PDDestination dest = item.getDestination();
                PDActionGoTo action = new PDActionGoTo();
                action.setDestination(dest);
                document.getDocumentCatalog().setOpenAction(action);
                document.save(args[1]);
            }
        }
    }
    
    private static void usage() {
        System.err.println("Usage: java " + GoToSecondBookmarkOnOpen.class.getName() +
                "<input-pdf> <output-pdf>");
    }
}
