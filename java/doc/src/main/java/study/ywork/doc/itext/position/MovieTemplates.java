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
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class MovieTemplates extends MovieCalendar {
    private static final String DEST = "movieTemplates.pdf";
    private final PdfFont font;

    public MovieTemplates() throws IOException {
        super();
        this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public static void main(String[] args) throws IOException {
        new MovieTemplates().manipulatePdf(DEST);
    }

    @Override
    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            doc.setProperty(Property.FONT, font);
            Text press = new Text("P").
                setFont(font).
                setFontSize(HEIGHT_LOCATION / 2).
                setFontColor(ColorConstants.WHITE);
            movieLocations = SqlUtils.getLocations();
            PdfFormXObject formUnder = new PdfFormXObject(new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth()));
            PdfFormXObject formOver = new PdfFormXObject(new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth()));
            PdfCanvas over = new PdfCanvas(formOver, pdfDoc);
            PdfCanvas under = new PdfCanvas(formUnder, pdfDoc);
            drawTimeTable(under);
            drawTimeSlots(over);

            List<Date> days = SqlUtils.getDays();
            List<Screening> screenings;
            PdfPage page;
            int d = 1;

            for (Date day : days) {
                page = pdfDoc.addNewPage();
                if (d != 1) {
                    doc.add(new AreaBreak());
                }

                new PdfCanvas(pdfDoc.getLastPage()).addXObjectAt(formUnder, 0, 0);
                new PdfCanvas(pdfDoc.getLastPage()).addXObjectAt(formOver, 0, 0);

                over = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
                under = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

                drawInfo(doc);
                drawDateInfo(day, d++, doc);
                screenings = SqlUtils.getScreenings(day);
                for (Screening screening : screenings) {
                    drawBlock(screening, under, over);
                    drawMovieInfo(screening, doc, press);
                }
            }
        } catch (SQLException | FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
