package study.ywork.doc.itext.color;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.Jbig2ImageData;
import com.itextpdf.io.image.TiffImageData;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;

import java.io.File;
import java.io.IOException;

public class PagedImages {
    private static final String DEST = "pagedImages.pdf";
    private static final String RESOURCE1 = "src/main/resources/images/marbles.tif";
    private static final String RESOURCE2 = "src/main/resources/images/amb.jb2";
    private static final String RESOURCE3 = "src/main/resources/images/animated_fox_dog.gif";

    public static void main(String[] args) throws IOException {
        new PagedImages().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            addTif(doc, RESOURCE1);
            doc.add(new AreaBreak());
            addJBIG2(doc, RESOURCE2);
            doc.add(new AreaBreak());
            addGif(doc, RESOURCE3);
        }
    }

    public static void addTif(Document document, String path) throws IOException {
        RandomAccessFileOrArray ra = new RandomAccessFileOrArray(
            new RandomAccessSourceFactory().createSource(new File(path).toURI().toURL()));
        int n = TiffImageData.getNumberOfPages(ra);
        Image img;
        for (int i = 1; i <= n; i++) {
            img = new Image(ImageDataFactory.createTiff(new File(path).toURI().toURL(), false, i, true));
            img.scaleToFit(523, 350);
            document.add(img);
        }
    }

    public static void addJBIG2(Document document, String path) throws IOException {
        RandomAccessFileOrArray ra = new RandomAccessFileOrArray(
            new RandomAccessSourceFactory().createSource(new File(path).toURI().toURL()));
        int n = Jbig2ImageData.getNumberOfPages(ra);
        Image img;
        for (int i = 1; i <= n; i++) {
            img = new Image(ImageDataFactory.createJbig2(new File(path).toURI().toURL(), i));
            img.scaleToFit(523, 350);
            document.add(img);
        }
    }

    public static void addGif(Document document, String path) throws IOException {
        int n = 10;
        Image img;
        for (int i = 1; i <= n; i++) {
            img = new Image(ImageDataFactory.createGifFrame(new File(path).toURI().toURL(), i));
            document.add(img);
        }
    }
}
