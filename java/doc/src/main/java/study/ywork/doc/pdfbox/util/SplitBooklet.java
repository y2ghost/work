package study.ywork.doc.pdfbox.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.io.IOException;

public class SplitBooklet {
    private SplitBooklet() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            usage();
            System.exit(-1);
        }

        try (PDDocument document = Loader.loadPDF(new File(args[0]));
             PDDocument outdoc = new PDDocument()) {
            for (PDPage page : document.getPages()) {
                PDRectangle cropBoxORIG = page.getCropBox();
                PDRectangle cropBoxLEFT = new PDRectangle(cropBoxORIG.getCOSArray());
                PDRectangle cropBoxRIGHT = new PDRectangle(cropBoxORIG.getCOSArray());

                if (page.getRotation() == 90 || page.getRotation() == 270) {
                    cropBoxLEFT.setUpperRightY(cropBoxORIG.getLowerLeftY() + cropBoxORIG.getHeight() / 2);
                    cropBoxRIGHT.setLowerLeftY(cropBoxORIG.getLowerLeftY() + cropBoxORIG.getHeight() / 2);
                } else {
                    cropBoxLEFT.setUpperRightX(cropBoxORIG.getLowerLeftX() + cropBoxORIG.getWidth() / 2);
                    cropBoxRIGHT.setLowerLeftX(cropBoxORIG.getLowerLeftX() + cropBoxORIG.getWidth() / 2);
                }

                if (page.getRotation() == 180 || page.getRotation() == 270) {
                    PDPage pageRIGHT = outdoc.importPage(page);
                    pageRIGHT.setCropBox(cropBoxRIGHT);
                    PDPage pageLEFT = outdoc.importPage(page);
                    pageLEFT.setCropBox(cropBoxLEFT);
                } else {
                    PDPage pageLEFT = outdoc.importPage(page);
                    pageLEFT.setCropBox(cropBoxLEFT);
                    PDPage pageRIGHT = outdoc.importPage(page);
                    pageRIGHT.setCropBox(cropBoxRIGHT);
                }
            }
            outdoc.save(args[1]);
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + SplitBooklet.class.getName() + " <input-pdf> <output-pdf>");
    }
}
