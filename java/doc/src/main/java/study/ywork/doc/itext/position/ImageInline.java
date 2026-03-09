package study.ywork.doc.itext.position;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class ImageInline {
    private static final String DEST = "imageInline.pdf";
    private static final String RESOURCE = "src/main/resources/images/loa.jpg";

    public static void main(String[] args) {
        new ImageInline().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        Rectangle postcard = new Rectangle(283, 416);
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            ImageData image = ImageDataFactory.create(RESOURCE);
            new PdfCanvas(pdfDoc.addNewPage(new PageSize(postcard))).
                    addImageAt(image, (postcard.getWidth() - image.getWidth()) / 2, (postcard.getHeight() - image.getHeight()) / 2, true);
        } catch (MalformedURLException | FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
