package study.ywork.doc.itext.color;

import com.itextpdf.barcodes.BarcodeEAN;
import com.itextpdf.barcodes.BarcodePDF417;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageHelper;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.awt.Toolkit;
import java.io.IOException;

public class ImageTypes {
    private static final String DEST = "imageTypes.pdf";
    private static final String[] RESOURCES = {
        "bruno_ingeborg.jpg",
        "map.jp2",
        "info.png",
        "close.bmp",
        "movie.gif",
        "butterfly.wmf",
        "animated_fox_dog.gif",
        "marbles.tif",
        "amb.jb2"
    };
    private static final String RESOURCE = "src/main/resources/images/hitchcock.png";

    public static void main(String[] args) throws IOException {
        new ImageTypes().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {

            Image img;
            for (int i = 0; i < RESOURCES.length; i++) {
                if (String.format("src/main/resources/img/%s", RESOURCES[i]).contains(".wmf")) {
                    WmfImageHelper wmf
                        = new WmfImageHelper(new WmfImageData(String.format("src/main/resources/images/%s", RESOURCES[i])));
                    img = new Image((PdfFormXObject) wmf.createFormXObject(pdfDoc));
                } else {
                    ImageData image
                        = ImageDataFactory.create(String.format("src/main/resources/images/%s", RESOURCES[i]));
                    img = new Image(new PdfImageXObject(image));
                }

                if (img.getImageWidth() > 300 || img.getImageHeight() > 300) {
                    img.scaleToFit(300, 300);
                }
                doc.add(new Paragraph(String.format("%s is an image of type %s", RESOURCES[i], img.getClass().getName())));
                doc.add(img);
            }

            java.awt.Image awtImage = Toolkit.getDefaultToolkit().createImage(RESOURCE);
            img = new Image(new PdfImageXObject(ImageDataFactory.create(awtImage, null)));
            doc.add(new Paragraph(String.format("%s is an image of type %s", "java.awt.Image", img.getClass().getName())));
            doc.add(img);

            BarcodeEAN codeEAN = new BarcodeEAN(pdfDoc);
            codeEAN.setCodeType(BarcodeEAN.EAN13);
            codeEAN.setCode("9781935182610");
            img = new Image(codeEAN.createFormXObject(null, null, pdfDoc));
            doc.add(new Paragraph(String.format("%s is an image of type %s", "Barcode", img.getClass().getName())));
            doc.add(img);

            BarcodePDF417 pdf417 = new BarcodePDF417();
            String text = "haha a text book";
            pdf417.setCode(text);
            PdfFormXObject xObject = pdf417.createFormXObject(null, pdfDoc);
            img = new Image(xObject);
            doc.add(new Paragraph(String.format("%s is an image of type %s", "Barcode", img.getClass().getName())));
            doc.add(img);
        }
    }
}
