package study.ywork.doc.itext.table;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileNotFoundException;

public class Spacing {
    private static final String DEST = "spacing.pdf";
    private static final String LONG_TEXT = "Dr. iText or: How I Learned to Stop Worrying " +
        "and Love the Portable Document Format.";

    public static void main(String[] args) {
        new Spacing().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            Cell cell = new Cell().add(new Paragraph(LONG_TEXT));
            table.addCell("default leading / spacing");
            table.addCell(cell);
            table.addCell("absolute leading: 20");

            cell = new Cell().add(new Paragraph(LONG_TEXT).setMultipliedLeading(0).setFixedLeading(20));
            table.addCell(cell);
            table.addCell("absolute leading: 3; relative leading: 1.2");
            cell = new Cell().add(new Paragraph(LONG_TEXT).setFixedLeading(3).setMultipliedLeading(1.2f));
            table.addCell(cell);
            table.addCell("absolute leading: 0; relative leading: 1.2");
            cell = new Cell().add(new Paragraph(LONG_TEXT).setFixedLeading(0).setMultipliedLeading(1.2f));
            table.addCell(cell);
            table.addCell("no leading at all");
            cell = new Cell().add(new Paragraph(LONG_TEXT).setFixedLeading(0).setMultipliedLeading(0));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Dr. iText or: How I Learned to Stop Worrying and Love PDF"));
            table.addCell("padding 10");
            cell.setPadding(10);
            table.addCell(cell);
            table.addCell("padding 0");
            cell.setPadding(0);
            table.addCell(cell.clone(true));
            table.addCell("different padding for left, right, top and bottom");
            cell.setPaddingLeft(20);
            cell.setPaddingRight(50);
            cell.setPaddingTop(0);
            cell.setPaddingBottom(5);
            table.addCell(cell.clone(true));
            doc.add(table);
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
