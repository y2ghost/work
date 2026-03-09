package study.ywork.doc.itext.table;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.FileNotFoundException;

public class RotationAndColors {
    private static final String DEST = "rotationAndColors.pdf";

    public static void main(String[] args) {
        new RotationAndColors().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3, 3}));
            Cell cell = new Cell().add(new Paragraph("COLOR"));
            cell.setRotationAngle(Math.toRadians(90));
            cell.setVerticalAlignment(VerticalAlignment.TOP);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("red / no borders"));
            cell.setBorder(Border.NO_BORDER);
            cell.setBackgroundColor(ColorConstants.RED);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("green / black bottom border"));
            cell.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 10f));
            cell.setBackgroundColor(ColorConstants.GREEN);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("cyan / blue top border + padding"));
            cell.setBorderTop(new SolidBorder(ColorConstants.BLUE, 5f));
            cell.setBackgroundColor(ColorConstants.CYAN);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("GRAY"));
            cell.setRotationAngle(Math.toRadians(90));
            cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("0.6"));
            cell.setBorder(Border.NO_BORDER);
            cell.setBackgroundColor(new DeviceGray(0.6f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("0.75"));
            cell.setBorder(Border.NO_BORDER);
            cell.setBackgroundColor(new DeviceGray(0.75f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("0.9"));
            cell.setBorder(Border.NO_BORDER);
            cell.setBackgroundColor(new DeviceGray(0.9f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("BORDERS"));
            cell.setRotationAngle(Math.toRadians(90));
            cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("different borders"));
            cell.setBorderLeft(new SolidBorder(ColorConstants.RED, 16f));
            cell.setBorderBottom(new SolidBorder(ColorConstants.ORANGE, 12f));
            cell.setBorderRight(new SolidBorder(ColorConstants.YELLOW, 8f));
            cell.setBorderTop(new SolidBorder(ColorConstants.GREEN, 4f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("with correct padding"));
            cell.setBorderLeft(new SolidBorder(ColorConstants.RED, 16f));
            cell.setBorderBottom(new SolidBorder(ColorConstants.ORANGE, 12f));
            cell.setBorderRight(new SolidBorder(ColorConstants.YELLOW, 8f));
            cell.setBorderTop(new SolidBorder(ColorConstants.GREEN, 4f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("red border"));
            cell.setBorder(new SolidBorder(ColorConstants.RED, 8f));
            table.addCell(cell);
            doc.add(table);
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
