package study.ywork.doc.itext.stream;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResizeImage {
    private static final String DEST = "resizeImage.pdf";
    private static final float FACTOR = 0.5f;
    private static final String SPECIAL_ID = "specialId.pdf";
    private static final String PRE_GENERATED_RESIZED_IMAGE = "src/main/resources/images/resizedImage.jpg";

    public static void main(String[] args) {
        boolean isLoadPreGeneratedImage = false;
        new ResizeImage().manipulatePdf(DEST, isLoadPreGeneratedImage);
    }

    public void manipulatePdf(String dest) {
        manipulatePdf(dest, true);
    }

    public void manipulatePdf(String dest, boolean isLoadPreGeneratedImage) {
        PdfName key = new PdfName("ITXT_SpecialId");
        PdfName value = new PdfName("123456789");
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SPECIAL_ID), new PdfWriter(dest))) {

            for (int i = 0; i < pdfDoc.getNumberOfPdfObjects(); i++) {
                PdfObject object = pdfDoc.getPdfObject(i);
                if (object == null || !object.isStream()) {
                    continue;
                }

                PdfStream stream = (PdfStream) object;
                if (value.equals(stream.get(key))) {
                    PdfImageXObject imageXObject = new PdfImageXObject(stream);
                    BufferedImage bi = imageXObject.getBufferedImage();
                    if (bi == null) {
                        continue;
                    }
                    int width = (int) (bi.getWidth() * FACTOR);
                    int height = (int) (bi.getHeight() * FACTOR);
                    byte[] imgBytes;
                    if (isLoadPreGeneratedImage) {
                        imgBytes = loadPreGeneratedImage();
                    } else {
                        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                        AffineTransform at = AffineTransform.getScaleInstance(FACTOR, FACTOR);
                        Graphics2D g = img.createGraphics();
                        g.drawRenderedImage(bi, at);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(img, "JPG", baos);
                        imgBytes = baos.toByteArray();
                    }
                    stream.clear();
                    stream.setData(imgBytes, false);
                    stream.put(PdfName.Type, PdfName.XObject);
                    stream.put(PdfName.Subtype, PdfName.Image);
                    stream.put(key, value);
                    stream.put(PdfName.Filter, PdfName.DCTDecode);
                    stream.put(PdfName.Width, new PdfNumber(width));
                    stream.put(PdfName.Height, new PdfNumber(height));
                    stream.put(PdfName.BitsPerComponent, new PdfNumber(8));
                    stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private byte[] loadPreGeneratedImage() throws IOException {
        return Files.readAllBytes(Paths.get(PRE_GENERATED_RESIZED_IMAGE));
    }
}
