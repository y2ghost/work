package study.ywork.doc.itext.table;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class XMen {
    private static final String DEST = "men.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";

    public static void main(String[] args) {
        new XMen().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            Image[] images = {
                new Image(ImageDataFactory.create(String.format(RESOURCE, "0120903"))),
                new Image(ImageDataFactory.create(String.format(RESOURCE, "0290334"))),
                new Image(ImageDataFactory.create(String.format(RESOURCE, "0376994"))),
                new Image(ImageDataFactory.create(String.format(RESOURCE, "0348150"))),
            };
            Table table = new Table(UnitValue.createPercentArray(6)).useAllAvailableWidth();
            table.addCell(new Cell()
                .add(new Paragraph("X-Men"))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.TOP));

            Cell cell = new Cell()
                .add(images[0])
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.TOP);
            table.addCell(cell);

            table.addCell(new Cell()
                .add(new Paragraph("X2"))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

            cell = new Cell()
                .add(images[1].setAutoScale(true))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
            table.addCell(cell);

            table.addCell(new Cell()
                .add(new Paragraph("X-Men: The Last Stand"))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.BOTTOM));

            table.addCell(new Cell()
                .add(images[2])
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.BOTTOM));

            table.addCell(new Cell()
                .add(new Paragraph("Superman Returns")));
            cell = new Cell();

            images[3].setWidth(UnitValue.createPercentValue(50));
            cell.add(images[3])
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.BOTTOM);
            table.addCell(cell);
            doc.add(table);
        } catch (MalformedURLException | FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
