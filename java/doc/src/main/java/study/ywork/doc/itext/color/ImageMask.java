package study.ywork.doc.itext.color;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import java.io.IOException;

public class ImageMask {
    private static final String[] RESULT = {
        "hardmask.pdf",
        "softmask.pdf"
    };
    private static final String DEST = RESULT[0];
    private static final String RESOURCE = "src/main/resources/images/bruno.jpg";

    public static void main(String[] args) throws IOException {
        new ImageMask().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        byte[] circledata = {(byte) 0x3c, (byte) 0x7e, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x7e,
            (byte) 0x3c};
        ImageData mask = ImageDataFactory.create(8, 8, 1, 1, circledata, null);
        mask.makeMask();
        mask.setInverted(true);
        createPdf(dest, mask);
        byte[] gradient = new byte[256];
        for (int i = 0; i < 256; i++)
            gradient[i] = (byte) i;
        mask = ImageDataFactory.create(256, 1, 1, 8, gradient, null);
        mask.makeMask();
        createPdf(RESULT[1], mask);
    }

    public void createPdf(String filename, ImageData mask) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
             Document doc = new Document(pdfDoc)) {
            ImageData img = ImageDataFactory.create(RESOURCE);
            img.setImageMask(mask);
            doc.add(new com.itextpdf.layout.element.Image(img));
        }
    }
}
