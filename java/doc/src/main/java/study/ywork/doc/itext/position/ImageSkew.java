package study.ywork.doc.itext.position;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class ImageSkew {
    private static final String DEST = "imageSkew.pdf";
    private static final String RESOURCE = "src/main/resources/images/loa.jpg";

    public static void main(String[] args) {
        new ImageSkew().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            ImageData img = ImageDataFactory.create(RESOURCE);
            new PdfCanvas(pdfDoc.addNewPage(new PageSize(416, 283))).
                    addImageWithTransformationMatrix(img, img.getWidth(), 0, .35f * img.getHeight(),
                            .65f * img.getHeight(), 30, 30);
        } catch (MalformedURLException | FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
