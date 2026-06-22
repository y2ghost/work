package study.ywork.doc.itext.position;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class MovieTextInfo extends MovieTimeBlocks {
    private static final String DEST = "movieTextInfo.pdf";
    private static final String[] TIME =
        {"09:30", "10:00", "10:30", "11:00", "11:30", "12:00",
            "00:30", "01:00", "01:30", "02:00", "02:30", "03:00",
            "03:30", "04:00", "04:30", "05:00", "05:30", "06:00",
            "06:30", "07:00", "07:30", "08:00", "08:30", "09:00",
            "09:30", "10:00", "10:30", "11:00", "11:30", "12:00",
            "00:30", "01:00"};
    private final PdfFont font;

    public MovieTextInfo() throws IOException {
        super();
        this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public static void main(String[] args) throws Exception {
        new MovieTextInfo().manipulatePdf(DEST);
    }

    @Override
    public void manipulatePdf(String dest) throws FileNotFoundException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc, new PageSize(PageSize.A4.getHeight(), PageSize.A4.getWidth()));
        doc.setMargins(0, 0, 0, 0);
        doc.setProperty(Property.FONT, font);
        Text press = new Text("P").
            setFont(font).
            setFontSize(HEIGHT_LOCATION / 2).
            setFontColor(ColorConstants.WHITE);

        try {
            movieLocations = SqlUtils.getLocations();
            List<Date> days = SqlUtils.getDays();
            List<Screening> screenings;
            int d = 1;
            for (Date day : days) {
                PdfPage page = pdfDoc.addNewPage();
                if (d != 1) {
                    doc.add(new AreaBreak());
                }
                PdfCanvas over = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
                PdfCanvas under = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

                drawTimeTable(under);
                drawTimeSlots(over);
                drawInfo(doc);
                drawDateInfo(day, d++, doc);
                screenings = SqlUtils.getScreenings(day);
                for (Screening screening : screenings) {
                    drawBlock(screening, under, over);
                    drawMovieInfo(screening, doc, press);
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        doc.close();
    }

    protected void drawInfo(Document doc) {
        float x = (OFFSET_LEFT + OFFSET_LOCATION) / 2;
        float y = OFFSET_BOTTOM + HEIGHT + 24;
        doc.add(new Paragraph("FOOBAR FILM FESTIVAL").setFontSize(18)
            .setFixedPosition(x, y, WIDTH).setMarginBottom(0).setMultipliedLeading(1)
            .setTextAlignment(TextAlignment.CENTER));

        x = OFFSET_LOCATION + WIDTH_LOCATION / 2f - 3;
        y = OFFSET_BOTTOM;
        doc.add(new Paragraph("The Majestic").setFontSize(18).setMarginBottom(0).setMultipliedLeading(1)
            .setFixedPosition(x, y, HEIGHT_LOCATION * 2)
            .setRotationAngle(Math.PI / 2)
            .setTextAlignment(TextAlignment.CENTER));

        y = OFFSET_BOTTOM + HEIGHT_LOCATION * 2;
        doc.add(new Paragraph("Googolplex").setFontSize(18).setMarginBottom(0).setMultipliedLeading(1)
            .setFixedPosition(x, y, HEIGHT_LOCATION * 4)
            .setRotationAngle(Math.PI / 2)
            .setTextAlignment(TextAlignment.CENTER));

        y = OFFSET_BOTTOM + HEIGHT_LOCATION * 6f;
        doc.add(new Paragraph("Cinema Paradiso").setFontSize(18).setMarginBottom(0).setMultipliedLeading(1)
            .setFixedPosition(x, y, HEIGHT_LOCATION * 3)
            .setRotationAngle(Math.PI / 2)
            .setTextAlignment(TextAlignment.CENTER));

        x = OFFSET_LOCATION + WIDTH_LOCATION - 6;
        for (int i = 0; i < LOCATION_COUNT; i++) {
            y = OFFSET_BOTTOM + ((8 - i) * HEIGHT_LOCATION);
            doc.add(new Paragraph(movieLocations.get(i)).setFontSize(12).setMarginBottom(0).setMultipliedLeading(1)
                .setFixedPosition(x, y, HEIGHT_LOCATION)
                .setRotationAngle(Math.PI / 2)
                .setTextAlignment(TextAlignment.CENTER));
        }

        y = OFFSET_BOTTOM + HEIGHT;
        for (int i = 0; i < TIMESLOTS; i++) {
            x = OFFSET_LEFT + (i * WIDTH_TIMESLOT);
            doc.add(new Paragraph(TIME[i]).setFontSize(6).setMarginBottom(0).setMultipliedLeading(1)
                .setFixedPosition(x, y, 100).setTextAlignment(TextAlignment.LEFT)
                .setRotationAngle(Math.PI / 4));
        }
    }

    protected void drawDateInfo(Date day, int d, Document doc) {
        float x = OFFSET_LOCATION;
        float y = OFFSET_BOTTOM + HEIGHT + 12;

        Paragraph p1 = new Paragraph("Day " + d).
            setFontSize(18).setMarginBottom(0).setMultipliedLeading(1).
            setFixedPosition(d, x, y, 100);

        x = OFFSET_LEFT;

        Paragraph p2 = new Paragraph(day.toString()).
            setFontSize(18).setMarginBottom(0).setMultipliedLeading(1).
            setFixedPosition(d, x, y, 100).
            setWidth(WIDTH).
            setTextAlignment(TextAlignment.RIGHT);

        doc.add(p1).add(p2);
    }

    protected void drawMovieInfo(Screening screening, Document doc, Text press) {
        if (screening.isPress()) {
            Rectangle rect = getPosition(screening);

            doc.showTextAligned(new Paragraph().add(press).setMargin(0), (rect.getLeft() + rect.getRight()) / 2,
                rect.getBottom() + rect.getHeight() / 4f, TextAlignment.CENTER);
        }
    }
}
