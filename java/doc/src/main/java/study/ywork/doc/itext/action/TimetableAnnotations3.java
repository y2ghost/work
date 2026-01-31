package study.ywork.doc.itext.action;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAnnotationBorder;
import com.itextpdf.kernel.pdf.PdfDashPattern;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSquareAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class TimetableAnnotations3 extends TimetableAnnotations1 {
    private static final String DEST = "timetableAnnotations3.pdf";
    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/movieTemplates.pdf";

    public static void main(String[] args) {
        TimetableAnnotations3 application = new TimetableAnnotations3();
        application.beforeManipulatePdf();
        application.manipulatePdf(DEST);
        application.afterManipulatePdf();
    }

    @Override
    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(MOVIE_TEMPLATES), new PdfWriter(DEST))) {
            int page = 1;
            PdfAnnotation annotation;
            LOCATION_NAMES = SqlUtils.getLocations();

            for (Date day : SqlUtils.getDays()) {
                for (Screening screening : SqlUtils.getScreenings(day)) {
                    Rectangle rect = getPosition(screening);
                    Movie movie = screening.getMovie();

                    if (screening.isPress()) {
                        annotation = new PdfStampAnnotation(rect)
                                .setStampName(new PdfName("NotForPublicRelease"))
                                .setContents("Press only")
                                .setColor(ColorConstants.BLACK.getColorValue())
                                .setFlags(PdfAnnotation.PRINT);
                    } else if (isSoldOut(screening)) {
                        float top = pdfDoc.getPage(page).getPageSize().getTop();
                        float[] line = new float[]{top - rect.getTop(), rect.getRight(),
                                top - rect.getBottom(), rect.getLeft()};
                        PdfDictionary borderStyleDict = new PdfDictionary();
                        borderStyleDict.put(PdfName.W, new PdfNumber(5));
                        borderStyleDict.put(PdfName.S, PdfName.B);
                        annotation = new PdfLineAnnotation(rect, line)
                                .setBorderStyle(borderStyleDict)
                                .setContents("SOLD OUT")
                                .setTitle(new PdfString(movie.getMovieTitle()))
                                .setColor(ColorConstants.GREEN.getColorValue())
                                .setFlags(PdfAnnotation.PRINT);
                    } else {
                        PdfAnnotationBorder borderArray = new PdfAnnotationBorder(0, 0, 2, new PdfDashPattern());
                        annotation = new PdfSquareAnnotation(rect)
                                .setContents("Tickets available")
                                .setTitle(new PdfString(movie.getMovieTitle()))
                                .setColor(ColorConstants.BLUE.getColorValue())
                                .setFlags(PdfAnnotation.PRINT)
                                .setBorder(borderArray.getPdfObject());
                    }
                    pdfDoc.getPage(page).addAnnotation(annotation);
                }
                page++;
            }
        } catch (SQLException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public boolean isSoldOut(Screening screening) {
        return screening.getMovie().getMovieTitle().startsWith("L");
    }
}
