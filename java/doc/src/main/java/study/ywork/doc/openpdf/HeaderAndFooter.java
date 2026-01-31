package study.ywork.doc.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class HeaderAndFooter extends PdfPageEventHelper {
    public static void main(String[] args) throws DocumentException, FileNotFoundException {
        Document doc = new Document(PageSize.A4, 36, 36, 65, 36);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("headerAndFooter.pdf"));
            writer.setPageEvent(new HeaderAndFooter());
            doc.open();
            Paragraph page1Body = new Paragraph("Page one content.");
            page1Body.setAlignment(Element.ALIGN_CENTER);
            doc.add(page1Body);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        doc.close();
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        PdfPTable table = new PdfPTable(3);
        table.setTotalWidth(510);
        table.setWidths(new int[]{38, 36, 36});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setPaddingBottom(5);
        table.getDefaultCell().setBorder(Rectangle.BOTTOM);

        PdfPCell emptyCell = new PdfPCell(new Paragraph(""));
        emptyCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(emptyCell);
        Paragraph title = new Paragraph("Header", new Font(Font.COURIER, 20, Font.BOLD));
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setPaddingBottom(10);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(titleCell);
        table.addCell(emptyCell);

        Font cellFont = new Font(Font.HELVETICA, 8);
        table.addCell(new Paragraph("Phone Number: 111-222-3333", cellFont));
        table.addCell(new Paragraph("Address : xxx, yyy, zzz", cellFont));
        table.addCell(new Paragraph("Website : https://www.example.com", cellFont));
        table.writeSelectedRows(0, -1, 34, 828, writer.getDirectContent());
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(510);
        table.setWidths(new int[]{50, 50});
        table.getDefaultCell().setPaddingBottom(5);
        table.getDefaultCell().setBorder(Rectangle.TOP);

        Paragraph title = new Paragraph("Footer", new Font(Font.HELVETICA, 10));
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setPaddingTop(4);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setBorder(Rectangle.TOP);
        table.addCell(titleCell);

        Paragraph pageNumberText = new Paragraph("Page " + document.getPageNumber(), new Font(Font.HELVETICA, 10));
        PdfPCell pageNumberCell = new PdfPCell(pageNumberText);
        pageNumberCell.setPaddingTop(4);
        pageNumberCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pageNumberCell.setBorder(Rectangle.TOP);
        table.addCell(pageNumberCell);
        table.writeSelectedRows(0, -1, 34, 36, writer.getDirectContent());
    }
}
