package study.ywork.doc.itext.table;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;

public class ColumnWidths {
    private static final String DEST = "columnWidths.pdf";

    public static void main(String[] args) throws IOException {
        new ColumnWidths().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Table table = createTable1();
            doc.add(table);
            table = createTable2();
            table.setMarginTop(5);
            table.setMarginBottom(5);
            doc.add(table);
            table = createTable3();
            doc.add(table);
            table = createTable4();
            table.setMarginTop(5);
            table.setMarginBottom(5);
            doc.add(table);
            table = createTable5();
            doc.add(table);
        }
    }

    public static Table createTable1() {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(288 / 5.23f));
        Cell cell = new Cell(1, 3).add(new Paragraph("Table 1"));
        table.addCell(cell);
        addLastCells(table);
        return table;
    }

    public static Table createTable2() {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}));
        table.setWidth(288);
        Cell cell = new Cell(1, 3).add(new Paragraph("Table 2"));
        table.addCell(cell);
        addLastCells(table);
        return table;
    }

    public static Table createTable3() {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(55.067f));
        Cell cell = new Cell(1, 3).add(new Paragraph("Table 3"));
        table.addCell(cell);
        addLastCells(table);
        return table;
    }

    public static Table createTable4() {
        Table table = new Table(new float[]{144, 72, 72});
        Rectangle rect = new Rectangle(523, 770);
        table.setWidth(UnitValue.createPercentValue((144 + 72 + 72) / rect.getWidth() * 100));
        Cell cell = new Cell(1, 3).add(new Paragraph("Table 4"));
        table.addCell(cell);
        addLastCells(table);
        return table;
    }

    public static Table createTable5() {
        Table table = new Table(new float[]{144, 72, 72});
        Cell cell = new Cell(1, 3).add(new Paragraph("Table 5"));
        table.addCell(cell);
        addLastCells(table);
        return table;
    }

    private static void addLastCells(Table table) {
        Cell cell = new Cell(2, 1).add(new Paragraph("Cell with rowspan 2"));
        table.addCell(cell);
        table.addCell("row 1; cell 1");
        table.addCell("row 1; cell 2");
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
    }
}
