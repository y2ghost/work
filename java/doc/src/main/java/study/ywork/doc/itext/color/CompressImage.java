package study.ywork.doc.itext.color;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.IOException;

public class CompressImage {
    private static final String[] RESULT = {
        "compressImage.pdf",
        "uncompressed.pdf",
        "compressed.pdf"
    };
    private static final String DEST = RESULT[0];
    private static final String RESOURCE = "src/main/resources/images/butterfly.bmp";

    public static void main(String[] args) throws IOException {
        new CompressImage().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        createPdf(RESULT[1], false);
        createPdf(RESULT[2], true);
        concatenateResults(dest, new String[]{RESULT[1], RESULT[2]});
    }

    public void createPdf(String filename, boolean compress) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
             Document doc = new Document(pdfDoc)) {
            Image img = new Image(ImageDataFactory.create(RESOURCE));
            if (compress) {
                img.getXObject().getPdfObject().setCompressionLevel(9);
            }
            doc.add(img);
        }
    }

    protected void concatenateResults(String dest, String[] names) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest))) {
            pdfDoc.initializeOutlines();
            for (String name : names) {
                try (PdfDocument tempDoc = new PdfDocument(new PdfReader(name))) {
                    tempDoc.copyPagesTo(1, tempDoc.getNumberOfPages(), pdfDoc);
                }
            }
        }
    }
}
