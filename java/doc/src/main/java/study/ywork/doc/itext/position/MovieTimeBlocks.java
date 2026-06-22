package study.ywork.doc.itext.position;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class MovieTimeBlocks extends MovieTimeTable {
    private static final String DEST = "movieTimeBlocks.pdf";
    private static final long TIME930 = Time.valueOf("09:30:00").getTime();
    private static final float MINUTE = WIDTH_TIMESLOT / 30f;
    protected List<String> movieLocations;

    public static void main(String[] args) throws Exception {
        new MovieTimeBlocks().manipulatePdf(DEST);
    }

    @Override
    public void manipulatePdf(String dest) throws FileNotFoundException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

        try {
            movieLocations = SqlUtils.getLocations();
            List<Date> days = SqlUtils.getDays();
            for (Date day : days) {
                PdfPage page = pdfDoc.addNewPage();
                PdfCanvas over = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
                PdfCanvas under = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

                drawTimeTable(under);
                drawTimeSlots(over);
                List<Screening> screenings = SqlUtils.getScreenings(day);
                for (Screening screening : screenings) {
                    drawBlock(screening, under, over);
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        pdfDoc.close();
    }

    protected void drawBlock(Screening screening, PdfCanvas under, PdfCanvas over) {
        under.saveState();
        DeviceRgb color = WebColors.getRGBColor(
            "#" + screening.getMovie().getEntry().getCategory().getColor());
        under.setFillColor(color);
        Rectangle rect = getPosition(screening);
        under.rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        under.fill();
        over.rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        over.stroke();
        under.restoreState();
    }

    protected Rectangle getPosition(Screening screening) {
        long minutesAfter930 = (screening.getTime().getTime() - TIME930) / 60000l;
        float llx = OFFSET_LEFT + (MINUTE * minutesAfter930);
        int location = movieLocations.indexOf(screening.getLocation()) + 1;
        float lly = OFFSET_BOTTOM + (LOCATION_COUNT - location) * HEIGHT_LOCATION;
        float width = MINUTE * screening.getMovie().getDuration();
        float height = HEIGHT_LOCATION;
        return new Rectangle(llx, lly, width, height);
    }
}
