package study.ywork.doc.pdfbox.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PrintTextLocations extends PDFTextStripper {

    public PrintTextLocations() {
        // NOOP
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                PDFTextStripper stripper = new PrintTextLocations();
                stripper.setSortByPosition(true);
                stripper.setStartPage(1);
                stripper.setEndPage(document.getNumberOfPages());

                Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream(), StandardCharsets.US_ASCII);
                stripper.writeText(document, dummy);
            }
        }
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
            System.out.println("String[" +
                    text.getXDirAdj() + "," + text.getYDirAdj() +
                    " font=" + text.getFont().getName() + ":" + text.getFontSize() +
                    " xscale=" + text.getXScale() + " height=" + text.getHeightDir() + " space=" +
                    text.getWidthOfSpace() + " width=" +
                    text.getWidthDirAdj() + "]" + text.getUnicode());
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + PrintTextLocations.class.getName() + " <input-pdf>");
    }
}
