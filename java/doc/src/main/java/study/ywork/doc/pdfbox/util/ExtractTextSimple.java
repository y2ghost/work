package study.ywork.doc.pdfbox.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class ExtractTextSimple {
    private ExtractTextSimple() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        }

        try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String text = stripper.getText(document);
                String pageStr = String.format("page %d:", p);
                System.out.println(pageStr);

                for (int i = 0; i < pageStr.length(); ++i) {
                    System.out.print("-");
                }

                System.out.println();
                System.out.println(text.trim());
                System.out.println();
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + ExtractTextSimple.class.getName() + " <input-pdf>");
        System.exit(-1);
    }
}
