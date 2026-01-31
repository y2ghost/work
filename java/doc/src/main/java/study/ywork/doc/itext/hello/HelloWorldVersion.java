package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class HelloWorldVersion {
    public static final String DEST = "helloWorldVersion.pdf";

    public static void main(String[] args) throws IOException {
        new HelloWorldVersion().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph("Hello World!"));
        doc.close();
    }
}
