package study.ywork.doc.itext.action;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimetableDestinations {
    private static final String DEST = "timetableDestinations.pdf";
    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/movieTemplates.pdf";
    private List<PdfAction> actions;

    public static void main(String[] args) {
        new TimetableDestinations().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(MOVIE_TEMPLATES), new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            int n = pdfDoc.getNumberOfPages();
            actions = new ArrayList<>();

            for (int i = 1; i <= n; i++) {
                actions.add(PdfAction.createGoTo(PdfExplicitDestination.createFit(pdfDoc.getPage(i))));
            }

            PdfFont symbol = PdfFontFactory.createFont(StandardFonts.SYMBOL);
            for (int i = 1; i <= n; i++) {
                doc.add(createNavigationTable(i, n).setFixedPosition(i, 696, 0, 120).setFont(symbol));
                if (n != i) {
                    doc.add(new AreaBreak());
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public Table createNavigationTable(int pageNumber, int total) {
        Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
        table.setWidth(120);
        Style style = new Style().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
        Link first = new Link(String.valueOf((char) 220), actions.get(0));
        table.addCell(new Cell().add(new Paragraph(first)).addStyle(style));
        Link previous = new Link(String.valueOf((char) 172), actions.get(pageNumber - 2 < 0 ? 0 : pageNumber - 2));
        table.addCell(new Cell().add(new Paragraph(previous)).addStyle(style));
        Link next = new Link(String.valueOf((char) 174), actions.get(pageNumber >= total ? total - 1 : pageNumber));
        table.addCell(new Cell().add(new Paragraph(next)).addStyle(style));
        Link last = new Link(String.valueOf((char) 222), actions.get(total - 1));
        table.addCell(new Cell().add(new Paragraph(last)).addStyle(style));
        return table;
    }
}
