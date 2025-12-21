package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

import java.io.File;
import java.io.IOException;

public class PrintBookmarks {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                PrintBookmarks meta = new PrintBookmarks();
                PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
                if (outline != null) {
                    meta.printBookmark(document, outline, "");
                } else {
                    System.out.println("This document does not contain any bookmarks");
                }
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + PrintBookmarks.class.getName() + " <input-pdf>");
    }

    public void printBookmark(PDDocument document, PDOutlineNode bookmark, String indentation) throws IOException {
        PDOutlineItem current = bookmark.getFirstChild();
        while (current != null) {
            PDDestination destination = current.getDestination();
            if (destination instanceof PDPageDestination pd) {
                System.out.println(indentation + "Destination page: " + (pd.retrievePageNumber() + 1));
            } else if (destination instanceof PDNamedDestination namedDestination) {
                PDPageDestination pd = document.getDocumentCatalog().findNamedDestinationPage(namedDestination);
                if (pd != null) {
                    System.out.println(indentation + "Destination page: " + (pd.retrievePageNumber() + 1));
                }
            } else if (destination != null) {
                System.out.println(indentation + "Destination class: " + destination.getClass().getSimpleName());
            }

            PDAction currentAction = current.getAction();
            if (currentAction instanceof PDActionGoTo gta) {
                if (gta.getDestination() instanceof PDPageDestination pd) {
                    System.out.println(indentation + "Destination page: " + (pd.retrievePageNumber() + 1));
                } else if (gta.getDestination() instanceof PDNamedDestination namedDestination) {
                    PDPageDestination pd = document.getDocumentCatalog().findNamedDestinationPage(namedDestination);
                    if (pd != null) {
                        System.out.println(indentation + "Destination page: " + (pd.retrievePageNumber() + 1));
                    }
                } else {
                    System.out.println(indentation + "Destination class: " + gta.getDestination().getClass().getSimpleName());
                }
            } else if (current.getAction() != null) {
                System.out.println(indentation + "Action class: " + current.getAction().getClass().getSimpleName());
            }

            System.out.println(indentation + current.getTitle());
            printBookmark(document, current, indentation + "    ");
            current = current.getNextSibling();
        }
    }
}
