package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class ReplaceURLs {
    private ReplaceURLs() {
    }

    public static void main(String[] args) throws IOException {
        PDDocument doc = null;
        try {
            if (args.length != 2) {
                usage();
            } else {
                doc = Loader.loadPDF(new File(args[0]));
                int pageNum = 0;

                for (PDPage page : doc.getPages()) {
                    pageNum++;
                    List<PDAnnotation> annotations = page.getAnnotations();

                    for (PDAnnotation annotation : annotations) {
                        PDAnnotation annot = annotation;
                        if (annot instanceof PDAnnotationLink link) {
                            PDAction action = link.getAction();
                            if (action instanceof PDActionURI uri) {
                                String oldURI = uri.getURI();
                                String newURI = "https://pdfbox.apache.org";
                                System.out.println("Page " + pageNum + ": Replacing " + oldURI + " with " + newURI);
                                uri.setURI(newURI);
                            }
                        }
                    }
                }

                doc.save(args[1]);
            }
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    private static void usage() {
        System.err.println("usage: " + ReplaceURLs.class.getName() + " <input-file> <output-file>");
    }
}
