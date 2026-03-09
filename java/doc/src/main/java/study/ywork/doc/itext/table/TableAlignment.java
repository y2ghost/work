package study.ywork.doc.itext.table;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;

public class TableAlignment {
    private static final String DEST = "tableAlignment.pdf";

    public static void main(String[] args) throws IOException {
        new TableAlignment().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Table table = createFirstTable();
            table.setWidth(UnitValue.createPercentValue(50));
            table.setHorizontalAlignment(HorizontalAlignment.LEFT);
            doc.add(table);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            doc.add(table);
            table.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            doc.add(table);
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
