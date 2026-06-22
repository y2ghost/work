package study.ywork.doc.pdfbox.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public final class ExtractTextByArea {
    private ExtractTextByArea() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                Rectangle rect = new Rectangle(10, 280, 275, 60);
                stripper.addRegion("class1", rect);
                PDPage firstPage = document.getPage(0);
                stripper.extractRegions(firstPage);
                System.out.println("Text in the area:" + rect);
                System.out.println(stripper.getTextForRegion("class1"));
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + ExtractTextByArea.class.getName() + " <input-pdf>");
    }
}
