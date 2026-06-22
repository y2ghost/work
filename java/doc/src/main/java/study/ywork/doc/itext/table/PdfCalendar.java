package study.ywork.doc.itext.table;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;

public class PdfCalendar {
    private static final String DEST = "pdfCalendar.pdf";
    protected static final int YEAR = 2011;
    protected static final String LANGUAGE = "en";
    protected static final String RESOURCE = "src/main/resources/calendar/%tm.jpg";
    protected static final String SPECIAL = "src/main/resources/calendar/%d.txt";
    protected static final String CONTENT_FILE = "src/main/resources/calendar/content.txt";
    protected static final Properties specialDays = new Properties();
    protected static final Properties content = new Properties();
    protected final PdfFont normal;
    protected final PdfFont bold;

    public PdfCalendar() throws IOException {
        this.normal = PdfFontFactory.createFont("src/main/resources/fonts/FreeSans.ttf",
                PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        this.bold = PdfFontFactory.createFont("src/main/resources/fonts/FreeSans.ttf",
                PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
    }

    public static void main(String[] args) throws IOException {
        new PdfCalendar().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        Locale locale = new Locale(LANGUAGE);
        createPdf(dest, locale, YEAR);
    }

    public void createPdf(String dest, Locale locale, int year) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate());
             FileInputStream fileIn1 = new FileInputStream(String.format(SPECIAL, YEAR));
             FileInputStream fileIn2 = new FileInputStream(CONTENT_FILE)) {
            specialDays.load(fileIn1);
            content.load(fileIn2);

            for (int month = 0; month < 12; month++) {
                Calendar calendar = new GregorianCalendar(year, month, 1);
                drawImageAndText(calendar, doc);
                Table table = new Table(UnitValue.createPercentArray(7)).useAllAvailableWidth();
                table.setWidth(504);
                table.addCell(getMonthCell(calendar, locale));
                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int day = 1;
                int position = 2;

                while (position != calendar.get(Calendar.DAY_OF_WEEK)) {
                    position = (position % 7) + 1;
                    table.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(ColorConstants.WHITE));
                }

                while (day <= daysInMonth) {
                    calendar = new GregorianCalendar(year, month, day++);
                    position = (position % 7) + 1;
                    table.addCell(getDayCell(calendar, locale));
                }

                while (position != 2) {
                    position = (position % 7) + 1;
                    table.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(ColorConstants.WHITE));
                }

                doc.add(table.setFixedPosition(169, 18, 504));
                if (11 != month) {
                    doc.add(new AreaBreak());
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void drawImageAndText(Calendar calendar, Document doc) throws IOException {
        Image img = new Image(ImageDataFactory.create(String.format(RESOURCE, calendar)));
        img.scaleToFit(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        img.setFixedPosition(
                (PageSize.A4.getHeight() - img.getImageScaledWidth()) / 2,
                (PageSize.A4.getWidth() - img.getImageScaledHeight()) / 2);
        doc.add(img);

        Paragraph p = new Paragraph(String.format("%s - \u00a9 Katharine Osborne",
                content.getProperty(String.format("%tm.jpg", calendar))))
                .setFont(normal)
                .setFontColor(new DeviceCmyk(0, 0, 0, 50))
                .setFontSize(8);
        doc.showTextAligned(p, 5, 5, calendar.get(Calendar.MONTH) + 1,
                TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        p = new Paragraph("Calendar generated using iText - example for the book iText in Action 2nd Edition")
                .setFont(normal)
                .setFontColor(new DeviceCmyk(0, 0, 0, 50))
                .setFontSize(8);
        doc.showTextAligned(p, 839, 5, calendar.get(Calendar.MONTH) + 1,
                TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
    }

    public Cell getMonthCell(Calendar calendar, Locale locale) {
        Cell cell = new Cell(1, 7);
        cell.setBackgroundColor(ColorConstants.WHITE);
        Paragraph p = new Paragraph(String.format(locale, "%1$tB %1$tY", calendar)).setFont(bold).setFontSize(14);
        p.setTextAlignment(TextAlignment.CENTER);
        cell.add(p);
        return cell;
    }

    public Cell getDayCell(Calendar calendar, Locale locale) {
        Cell cell = new Cell();
        cell.setPadding(3);

        if (isSunday(calendar)) {
            cell.setBackgroundColor(ColorConstants.GRAY);
        } else if (isSpecialDay(calendar)) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        } else {
            cell.setBackgroundColor(ColorConstants.WHITE);
        }

        Text text = new Text(String.format(locale, "%1$ta", calendar)).setFont(normal).setFontSize(8);
        text.setTextRise(8);
        Paragraph p = new Paragraph(text);
        p.addTabStops(new TabStop(100, TabAlignment.RIGHT));
        p.add(new Tab());
        p.add(new Text(String.format(locale, "%1$te", calendar)).setFont(normal).setFontSize(16));
        cell.add(p);
        return cell;
    }

    public boolean isSunday(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public boolean isSpecialDay(Calendar calendar) {
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return true;
        }

        return specialDays.containsKey(String.format("%1$tm%1$td", calendar));
    }
}
