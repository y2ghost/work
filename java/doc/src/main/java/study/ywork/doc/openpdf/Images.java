package study.ywork.doc.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class Images {
    public static void main(String[] args) {
        System.out.println("Images");
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("images.pdf"));
            document.open();
            document.add(new Paragraph("A picture of my dog: otsoe.jpg"));
            Image jpg = Image.getInstanceFromClasspath("images/otsoe.jpg");
            document.add(jpg);
            document.add(new Paragraph("getacro.gif"));
            Image gif = Image.getInstanceFromClasspath("images/getacro.gif");
            document.add(gif);
            document.add(new Paragraph("pngnow.png"));
            Image png = Image.getInstanceFromClasspath("images/pngnow.png");
            document.add(png);
            document.add(new Paragraph("grayscaled.png"));
            Image grayscaledPng = Image.getInstanceFromClasspath("images/grayscaled.png");
            document.add(grayscaledPng);
            document.add(new Paragraph("iText.bmp"));
            Image bmp = Image.getInstanceFromClasspath("images/iText.bmp");
            document.add(bmp);
            document.add(new Paragraph("iText.wmf"));
            Image wmf = Image.getInstanceFromClasspath("images/iText.wmf");
            document.add(wmf);
            document.add(new Paragraph("iText.tif"));
            Image tif = Image.getInstanceFromClasspath("images/iText.tif");
            document.add(tif);
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }

        document.close();
    }
}