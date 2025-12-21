package study.ywork.doc.itext.action;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class TimetableAnnotations2 extends TimetableAnnotations1 {
    private static final String DEST = "timetableAnnotations2.pdf";
    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/movieTemplates.pdf";
    public static final String IMDB = "https://imdb.com/title/tt%s/";

    public static void main(String[] args) {
        TimetableAnnotations2 application = new TimetableAnnotations2();
        application.beforeManipulatePdf();
        application.manipulatePdf(DEST);
        application.afterManipulatePdf();
    }

    @Override
    public void manipulatePdf(String dest) {
        try (PdfReader reader = new PdfReader(MOVIE_TEMPLATES);
             PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(dest))) {
            int page = 1;
            LOCATION_NAMES = SqlUtils.getLocations();

            for (Date day : SqlUtils.getDays()) {
                for (Screening screening : SqlUtils.getScreenings(day)) {
                    Rectangle rect = getPosition(screening);
                    PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
                    annotation.setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT);
                    annotation.setAction(PdfAction.createURI(String.format(IMDB, screening.getMovie().getImdb())));
                    annotation.setBorder(new PdfArray(new float[]{0, 0, 1}));
                    pdfDoc.getPage(page).addAnnotation(annotation);
                }

                page++;
            }
        } catch (SQLException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
