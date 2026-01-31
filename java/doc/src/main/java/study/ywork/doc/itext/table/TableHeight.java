package study.ywork.doc.itext.table;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileNotFoundException;

public class TableHeight {
    private static final String DEST = "tableHeight.pdf";

    public static void main(String[] args) {
        new TableHeight().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Table table = createFirstTable();
            doc.add(table);
            table = createFirstTable();
            table.setWidth(50);
            doc.add(table);
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static Table createFirstTable() {
        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        Cell cell = new Cell(1, 3).add(new Paragraph("Cell with colspan 3"));
        table.addCell(cell);
        cell = new Cell(2, 1).add(new Paragraph("Cell with rowspan 2"));
        table.addCell(cell);
        table.addCell("row 1; cell 1");
        table.addCell("row 1; cell 2");
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
        return table;
    }
}
