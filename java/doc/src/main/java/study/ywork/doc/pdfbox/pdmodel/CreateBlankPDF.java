package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;

public final class CreateBlankPDF {
    private CreateBlankPDF() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: " + CreateBlankPDF.class.getName() + " <outputfile.pdf>");
            System.exit(1);
        }

        String filename = args[0];
        try (PDDocument doc = new PDDocument()) {
            // 有效的PDF文档至少需要一页
            PDPage blankPage = new PDPage();
            doc.addPage(blankPage);
            doc.save(filename);
        }
    }
}
