package study.ywork.doc.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextExtraction {
    public static void main(String[] args) {
        System.out.println("Text extraction");
        Document document = new Document();
        ByteArrayOutputStream baos = writeTextToDocument(document);

        if (null == baos) {
            System.err.println("error write text!");
            return;
        }

        try {
            PdfReader reader = new PdfReader(baos.toByteArray());
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
            System.out.println("Page 1 text: " + pdfTextExtractor.getTextFromPage(1));
            System.out.println("Page 2 text: " + pdfTextExtractor.getTextFromPage(2));
            System.out.println("Page 3 table cell text: " + pdfTextExtractor.getTextFromPage(3));

        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }
    }

    private static ByteArrayOutputStream writeTextToDocument(Document document) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            writer.getInfo().put(PdfName.CREATOR, new PdfString(Document.getVersion()));
            document.add(new Paragraph("Text to extract"));

            document.newPage();
            document.add(new Paragraph("Text on page 2"));

            document.newPage();
            PdfPTable table = new PdfPTable(3);
            table.addCell("Cell 1");
            table.addCell("Cell 2");
            table.addCell("Cell 3");
            document.add(table);
            document.close();

            try (FileOutputStream fos = new FileOutputStream("textExtraction.pdf")) {
                fos.write(baos.toByteArray());
            }
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }

        return baos;
    }
}
