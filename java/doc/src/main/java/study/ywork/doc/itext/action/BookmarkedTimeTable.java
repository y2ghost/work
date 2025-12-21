package study.ywork.doc.itext.action;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class BookmarkedTimeTable {
    private static final String DEST = "bookmarkedTimeTable.pdf";
    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/movieTemplates.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        BookmarkedTimeTable application = new BookmarkedTimeTable();
        application.manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        try (PdfReader reader = new PdfReader(MOVIE_TEMPLATES);
             PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(dest))) {
            PdfOutline root = pdfDoc.getOutlines(false);
            root.setTitle("Calendar");
            int page = 1;
            List<Date> days = SqlUtils.getDays();

            for (Date day : days) {
                PdfOutline kid = root.addOutline(day.toString());
                kid.addAction(PdfAction.createGoTo(PdfExplicitDestination.createFit(pdfDoc.getPage(page))));
                page++;
            }
        }
    }
}
