package study.ywork.doc.itext.action;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.TimeZone;

public class TimetableAnnotations1 {
    protected List<String> LOCATION_NAMES;
    private static final int LOCATIONS = 9;
    private static final int TIMESLOTS = 32;
    private static final long TIME930 = 30600000l;
    private static final float OFFSET_LEFT = 76;
    private static final float WIDTH = 740;
    private static final float WIDTH_TIMESLOT = WIDTH / TIMESLOTS;
    private static final float MINUTE = WIDTH_TIMESLOT / 30f;
    private static final float OFFSET_BOTTOM = 36;
    private static final float HEIGHT = 504;
    private static final float HEIGHT_LOCATION = HEIGHT / LOCATIONS;
    private static final String DEST = "timetableAnnotations1.pdf";
    private static final String INFO = "Movie produced in %s; run length: %s";

    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/movieTemplates.pdf";
    private static TimeZone CURRENT_USER_TIME_ZONE;

    protected void beforeManipulatePdf() {
        CURRENT_USER_TIME_ZONE = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Brussels"));
    }

    protected void afterManipulatePdf() {
        TimeZone.setDefault(CURRENT_USER_TIME_ZONE);
    }

    public static void main(String[] args) throws IOException, SQLException {

        TimetableAnnotations1 application = new TimetableAnnotations1();
        application.beforeManipulatePdf();
        application.manipulatePdf(DEST);
        application.afterManipulatePdf();
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        try (PdfReader reader = new PdfReader(MOVIE_TEMPLATES);
             PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(dest))) {
            int page = 1;
            LOCATION_NAMES = SqlUtils.getLocations();

            for (Date day : SqlUtils.getDays()) {
                for (Screening screening : SqlUtils.getScreenings(day)) {
                    Movie movie = screening.getMovie();
                    Rectangle rect = getPosition(screening);
                    PdfAnnotation annotation = new PdfTextAnnotation(rect);
                    annotation.setTitle(new PdfString(movie.getMovieTitle()));
                    annotation.setContents(String.format(INFO, movie.getYear(), movie.getDuration()));
                    annotation.setName(new PdfString("Help"));
                    annotation.put(PdfName.Name, new PdfString("Help"));
                    DeviceRgb baseColor = WebColors.getRGBColor(
                            "#" + movie.getEntry().getCategory().getColor());
                    annotation.setColor(baseColor);
                    pdfDoc.getPage(page).addAnnotation(annotation);
                }

                page++;
            }
        }
    }

    protected Rectangle getPosition(Screening screening) {
        long minutesAfter930 = (screening.getTime().getTime() - TIME930) / 60000l;
        float llx = OFFSET_LEFT + (MINUTE * minutesAfter930);
        int location = LOCATION_NAMES.indexOf(screening.getLocation()) + 1;
        float lly = OFFSET_BOTTOM + (LOCATIONS - location) * HEIGHT_LOCATION;
        float urx = llx + MINUTE * screening.getMovie().getDuration();
        float ury = lly + HEIGHT_LOCATION;
        Rectangle rect = new Rectangle(llx, lly, urx - llx, ury - lly);
        return rect;
    }
}
