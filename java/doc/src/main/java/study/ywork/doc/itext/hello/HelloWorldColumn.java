package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;

public class HelloWorldColumn {
    private static final String DEST = "helloWorldColumn.pdf";

    public static void main(String[] args) throws IOException {
        new HelloWorldColumn().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        try (PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {
            doc.showTextAligned("Hello World", 36, 788, TextAlignment.LEFT);
        }
    }
}
