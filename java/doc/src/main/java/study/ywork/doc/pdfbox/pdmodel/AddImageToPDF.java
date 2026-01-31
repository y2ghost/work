package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

public class AddImageToPDF {
    public void createPDFFromImage(String inputFile, String imagePath, String outputFile)
            throws IOException {
        try (PDDocument doc = Loader.loadPDF(new File(inputFile))) {
            PDPage page = doc.getPage(0);
            // 可以使用 LosslessFactory.createFromImage() 加载BufferedImage图片对象
            PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true)) {
                float scale = 1f;
                contentStream.drawImage(pdImage, 20, 20, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
            }
            doc.save(outputFile);
        }
    }

    public static void main(String[] args) throws IOException {
        AddImageToPDF app = new AddImageToPDF();
        if (args.length != 3) {
            app.usage();
        } else {
            app.createPDFFromImage(args[0], args[1], args[2]);
        }
    }

    private void usage() {
        System.err.println("usage: " + this.getClass().getName() + " <input-pdf> <image> <output-pdf>");
    }
}
