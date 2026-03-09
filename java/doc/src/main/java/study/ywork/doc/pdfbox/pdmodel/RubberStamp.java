package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationRubberStamp;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class RubberStamp {
    private RubberStamp() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                if (document.isEncrypted()) {
                    throw new IOException("Encrypted documents are not supported for this example");
                }

                for (PDPage page : document.getPages()) {
                    List<PDAnnotation> annotations = page.getAnnotations();
                    PDAnnotationRubberStamp rs = new PDAnnotationRubberStamp();
                    rs.setName(PDAnnotationRubberStamp.NAME_TOP_SECRET);
                    rs.setRectangle(new PDRectangle(100, 100));
                    rs.setContents("A top secret note");
                    annotations.add(rs);
                }

                document.save(args[1]);
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + RubberStamp.class.getName() + " <input-pdf> <output-pdf>");
    }
}
