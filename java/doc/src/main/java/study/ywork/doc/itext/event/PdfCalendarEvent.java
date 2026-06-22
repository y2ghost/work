package study.ywork.doc.itext.event;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TableRenderer;
import study.ywork.doc.itext.table.PdfCalendar;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PdfCalendarEvent extends PdfCalendar {
    private static final String DEST = "pdfCalendarEvent.pdf";
    private final float[] cmykGreen = new float[]{1, 0, 1, 0};
    private final float[] cmykGray = new float[]{0, 0, 0, 50f / 255};
    private final float[] cmykWhite = new float[]{0, 0, 0, 0};
    private final float[] cmykYellow = new float[]{0, 0, 1, 15f / 255};

    public PdfCalendarEvent() throws IOException {
        super();
    }

    public static void main(String[] args) throws IOException {
        new PdfCalendarEvent().manipulatePdf(DEST);
    }

    @Override
    public void manipulatePdf(String dest) {
        Locale locale = new Locale(LANGUAGE);
        createPdf(dest, locale, YEAR);
    }

    @Override
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
                table.setBackgroundColor(ColorConstants.YELLOW);
                table.setWidth(504);
                table.setNextRenderer(new RoundedTableRenderer(table, new Table.RowRange(0, 6)));
                table.addCell(getMonthCell(calendar, locale));
                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int day = 1;
                int position = 2;

                while (position != calendar.get(Calendar.DAY_OF_WEEK)) {
                    position = (position % 7) + 1;
                    Cell cell = new Cell().add(new Paragraph(""));
                    cell.setNextRenderer(new RoundedCellRenderer(cell, cmykWhite, false));
                    table.addCell(cell);
                }

                while (day <= daysInMonth) {
                    calendar = new GregorianCalendar(year, month, day++);
                    position = (position % 7) + 1;
                    table.addCell(getDayCell(calendar, locale));
                }

                while (position != 2) {
                    position = (position % 7) + 1;
                    Cell cell = new Cell().add(new Paragraph(""));
                    cell.setNextRenderer(new RoundedCellRenderer(cell, cmykWhite, false));
                    table.addCell(cell);
                }

                doc.add(table.setFixedPosition(169, 18, 504));
                if (11 != month) {
                    doc.add(new AreaBreak());
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
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
                .setFontColor(new DeviceCmyk(cmykGray[0], cmykGray[1], cmykGray[2], cmykGray[3]))
                .setFontSize(8);
        doc.showTextAligned(p, 5, 5, calendar.get(Calendar.MONTH) + 1,
                TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        p = new Paragraph("Calendar generated using iText - example for the book iText in Action 2nd Edition")
                .setFont(normal)
                .setFontColor(new DeviceCmyk(cmykGray[0], cmykGray[1], cmykGray[2], cmykGray[3]))
                .setFontSize(8);
        doc.showTextAligned(p, 837, 5, calendar.get(Calendar.MONTH) + 1,
                TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
    }

    @Override
    public Cell getMonthCell(Calendar calendar, Locale locale) {
        Cell cell = new Cell(1, 7);
        cell.setNextRenderer(new RoundedCellRenderer(cell, cmykYellow, false));
        Paragraph p = new Paragraph(String.format(locale, "%1$tB %1$tY", calendar)).setFont(bold).setFontSize(14);
        p.setHorizontalAlignment(HorizontalAlignment.CENTER);
        cell.add(p);
        return cell;
    }

    @Override
    public Cell getDayCell(Calendar calendar, Locale locale) {
        Cell cell = new Cell();
        cell.setPadding(10);
        cell.setBackgroundColor(ColorConstants.WHITE);
        if (isSunday(calendar) || isSpecialDay(calendar)) {
            cell.setBorder(Border.NO_BORDER);
            cell.setNextRenderer(new RoundedCellRenderer(cell, cmykGreen, true));
        } else {
            cell.setNextRenderer(new RoundedCellRenderer(cell, cmykWhite, true));
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

    @Override
    public boolean isSunday(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    @Override
    public boolean isSpecialDay(Calendar calendar) {
        return specialDays.containsKey(String.format("%1$tm%1$td", calendar));
    }

    protected class RoundedTableRenderer extends TableRenderer {
        public RoundedTableRenderer(Table modelElement, Table.RowRange rowRange) {
            super(modelElement, rowRange);
        }

        @Override
        public void drawBackground(DrawContext drawContext) {
            Rectangle rect = getOccupiedAreaBBox();
            drawContext.getCanvas()
                    .saveState()
                    .roundRectangle(rect.getX(), rect.getBottom(), rect.getWidth(), rect.getHeight(), 4)
                    .setFillColorCmyk(cmykYellow[0], cmykYellow[1], cmykYellow[2], cmykYellow[3])
                    .setStrokeColorCmyk(cmykYellow[0], cmykYellow[1], cmykYellow[2], cmykYellow[3])
                    .fillStroke()
                    .restoreState();

        }

        @Override
        public IRenderer getNextRenderer() {
            return new RoundedTableRenderer((Table) modelElement, rowRange);
        }

        @Override
        protected void drawBorders(DrawContext drawContext) {
            // NOOP
        }
    }

    protected class RoundedCellRenderer extends CellRenderer {
        protected float[] cmykColor;
        protected boolean isColoredBackground;

        public RoundedCellRenderer(Cell modelElement, float[] cmykColor, boolean isColoredBackground) {
            super(modelElement);
            this.cmykColor = cmykColor;
            this.isColoredBackground = isColoredBackground;
        }

        @Override
        public IRenderer getNextRenderer() {
            return new RoundedCellRenderer((Cell) modelElement, cmykColor, isColoredBackground);
        }

        @Override
        public void drawBackground(DrawContext drawContext) {
            Rectangle rect = getOccupiedAreaBBox();
            PdfCanvas canvas = drawContext.getCanvas();
            canvas.saveState()
                    .roundRectangle(rect.getLeft() + 2.5f, rect.getBottom() + 2.5f, rect.getWidth() - 5, rect.getHeight() - 5, 10)
                    .setStrokeColorCmyk(cmykColor[0], cmykColor[1], cmykColor[2], cmykColor[3])
                    .setLineWidth(1.5f);
            if (isColoredBackground) {
                canvas.setFillColor(new DeviceCmyk(0, 0, 0, 0)).fillStroke();
            } else {
                canvas.stroke();
            }
            canvas.restoreState();

        }

        @Override
        public void drawBorder(DrawContext drawContext) {
            // NOOP
        }
    }
}
