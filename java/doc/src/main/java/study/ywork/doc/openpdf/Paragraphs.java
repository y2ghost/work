package study.ywork.doc.openpdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class Paragraphs {
    public static void main(String[] args) {
        System.out.println("the Paragraph object");
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("paragraphs.pdf"));
            document.open();
            Paragraph p1 = new Paragraph(new Chunk(
                "This is my first paragraph. ",
                FontFactory.getFont(FontFactory.HELVETICA, 10)));
            p1.add("The leading of this paragraph is calculated automagically. ");
            p1.add("The default leading is 1.5 times the fontsize. ");
            p1.add(new Chunk("You can add chunks "));
            p1.add(new Phrase("or you can add phrases. "));
            p1.add(new Phrase(
                "Unless you change the leading with the method setLeading, the leading doesn't change if you add text with another leading. This can lead to some problems.",
                FontFactory.getFont(FontFactory.HELVETICA, 18)));
            document.add(p1);
            Paragraph p2 = new Paragraph(new Phrase(
                "This is my second paragraph. ", FontFactory.getFont(
                FontFactory.HELVETICA, 12)));
            p2.add("As you can see, it started on a new line.");
            document.add(p2);
            Paragraph p3 = new Paragraph("This is my third paragraph.",
                FontFactory.getFont(FontFactory.HELVETICA, 12));
            document.add(p3);
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }

        document.close();
    }
}
