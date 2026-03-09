package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HelloWorldMemory {
    private static final String DEST = "helloWorldMemory.pdf";

    public static void main(String[] args) throws IOException {
        new HelloWorldMemory().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);

        try (PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph("Hello World!"));
        }

        try (FileOutputStream fos = new FileOutputStream(DEST)) {
            fos.write(baos.toByteArray());
        }
    }
}
