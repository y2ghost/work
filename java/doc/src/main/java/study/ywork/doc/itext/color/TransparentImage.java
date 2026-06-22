package study.ywork.doc.itext.color;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.IOException;

public class TransparentImage {
    private static final String DEST = "transparentImage.pdf";
    private static final String RESOURCE1 = "src/main/resources/images/bruno.jpg";
    private static final String RESOURCE2 = "src/main/resources/images/info.png";
    private static final String RESOURCE3 = "src/main/resources/images/1t3xt.gif";
    private static final String RESOURCE4 = "src/main/resources/images/logo.gif";

    public static void main(String[] args) throws IOException {
        new TransparentImage().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        Image img1 = new Image(ImageDataFactory.create(RESOURCE1));
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(img1.getImageScaledWidth(),
                 img1.getImageScaledHeight()))) {
            img1.setFixedPosition(0, 0);
            doc.add(img1);
            Image img2 = new Image(ImageDataFactory.create(RESOURCE2));
            img2.setFixedPosition(0, 260);
            doc.add(img2);
            ImageData img3basics = ImageDataFactory.create(RESOURCE3);
            img3basics.setTransparency(new int[]{0x00, 0x10});
            Image img3 = new Image(img3basics);
            img3.setFixedPosition(0, 0);
            doc.add(img3);
            ImageData img4basics = ImageDataFactory.create(RESOURCE4);
            img4basics.setTransparency(new int[]{0xF0, 0xFF});
            Image img4 = new Image(img4basics);
            img4.setFixedPosition(50, 50);
            doc.add(img4);
        }
    }
}
