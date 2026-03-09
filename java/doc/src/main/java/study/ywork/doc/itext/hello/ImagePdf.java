package study.ywork.doc.itext.hello;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class ImagePdf {
    private static final String DOG = "src/main/resources/images/dog.bmp";
    private static final String FOX = "src/main/resources/images/fox.bmp";
    private static final String DEST = "image.pdf";

    public static void main(String[] args) throws IOException {
        new ImagePdf().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        try (Document doc = new Document(pdf)) {
            Image fox = new Image(ImageDataFactory.create(FOX));
            Image dog = new Image(ImageDataFactory.create(DOG));
            Paragraph p = new Paragraph("The fox image ")
                    .add(fox)
                    .add(" jumps over ")
                    .add(dog);
            doc.add(p);
        }
    }
}
