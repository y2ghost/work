package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class HelloWorldMaximum {
    private static final String DEST = "helloWorldMaximum.pdf";

    public static void main(String[] args) throws IOException {
        new HelloWorldMaximum().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        PageSize pageSize = new PageSize(14400, 14400);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, pageSize);
        doc.add(new Paragraph("Hello World!"));
        pdfDoc.getPage(1).getPdfObject().put(PdfName.UserUnit, new PdfNumber(75000f));
        doc.close();
    }
}
