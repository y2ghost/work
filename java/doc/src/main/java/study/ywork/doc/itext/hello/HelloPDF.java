package study.ywork.doc.itext.hello;


import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileNotFoundException;

public class HelloPDF {
    public static void main(String[] args) throws FileNotFoundException {
        HelloPDF helloPDF = new HelloPDF();
        helloPDF.createPdf();
        helloPDF.createNarrowPdf();
    }

    public void createPdf() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter("hello-pdf.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);
        doc.add(new Paragraph("Hello PDF!"));
        doc.close();
    }

    public void createNarrowPdf() throws FileNotFoundException {
        PageSize pageSize = new PageSize(new Rectangle(216f, 720f));
        PdfWriter pdfWriter = new PdfWriter("hello-narrow.pdf");
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document doc = new Document(pdfDocument, pageSize);
        doc.setMargins(108f, 72f, 180f, 36f);
        doc.add(new Paragraph("Hello World! Hello People! "
            + "Hello Sky! Hello Sun! Hello Moon! Hello Stars!"));
        doc.close();
    }
}
