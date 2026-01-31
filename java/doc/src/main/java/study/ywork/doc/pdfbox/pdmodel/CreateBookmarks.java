package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PageMode;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import java.io.File;
import java.io.IOException;

public final class CreateBookmarks {
    private CreateBookmarks() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                if (document.isEncrypted()) {
                    System.err.println("Error: Cannot add bookmarks to encrypted document.");
                    System.exit(1);
                }

                PDDocumentOutline outline = new PDDocumentOutline();
                document.getDocumentCatalog().setDocumentOutline(outline);
                PDOutlineItem pagesOutline = new PDOutlineItem();
                pagesOutline.setTitle("All Pages");
                outline.addLast(pagesOutline);
                int pageNum = 0;

                for (PDPage page : document.getPages()) {
                    pageNum++;
                    PDPageDestination dest = new PDPageFitWidthDestination();
                    dest.setPage(page);
                    PDOutlineItem bookmark = new PDOutlineItem();
                    bookmark.setDestination(dest);
                    bookmark.setTitle("Page " + pageNum);
                    pagesOutline.addLast(bookmark);
                }

                pagesOutline.openNode();
                outline.openNode();
                document.getDocumentCatalog().setPageMode(PageMode.USE_OUTLINES);
                document.save(args[1]);
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + CreateBookmarks.class.getName() + " <input-pdf> <output-pdf>");
    }
}
