package study.ywork.doc.openpdf;


import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class HelloWorld {
    public static void main(String[] args) {
        Document document = new Document();
        try {
            PdfWriter instance = PdfWriter.getInstance(document, new FileOutputStream("helloWorld.pdf"));
            document.open();
            instance.getInfo().put(PdfName.CREATOR, new PdfString(Document.getVersion()));
            document.add(new Paragraph("Hello World"));
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }

        document.close();
    }
}
