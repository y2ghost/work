package study.ywork.doc.itext.color;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class CompressAwt {
    private static final String RESOURCE = "src/main/resources/images/hitchcock.png";
    private static final String[] RESULT = {
        "compressAwt_hitchcock100.pdf",
        "compressAwt_hitchcock20.pdf",
        "compressAwt_hitchcock10.pdf"
    };

    public static void main(String[] args) throws IOException {
        new CompressAwt().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        createPdf(RESULT[0], 1);
        createPdf(RESULT[1], 0.2f);
        createPdf(RESULT[2], 0.1f);
    }

    public void createPdf(String dest, float quality) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(200, 280))) {
            BufferedImage bi = ImageIO.read(new FileInputStream(RESOURCE));
            ByteArrayOutputStream outputStream;

            try {
                ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
                jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpgWriteParam.setCompressionQuality(quality);

                outputStream = new ByteArrayOutputStream();
                jpgWriter.setOutput(new MemoryCacheImageOutputStream((outputStream)));
                IIOImage outputImage = new IIOImage(bi, null, null);

                jpgWriter.write(null, outputImage, jpgWriteParam);
                jpgWriter.dispose();
                outputStream.flush();
            } catch (Exception e) {
                throw new PdfException(e);
            }
            Image img = new Image(ImageDataFactory.create(outputStream.toByteArray()));
            img.setFixedPosition(15, 15);

            doc.add(img);
        }
    }
}
