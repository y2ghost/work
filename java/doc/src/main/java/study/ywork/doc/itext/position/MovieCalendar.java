package study.ywork.doc.itext.position;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import study.ywork.doc.domain.Screening;

import java.io.IOException;

public class MovieCalendar extends MovieTextInfo {
    private static final String DEST = "movieCalendar.pdf";

    public MovieCalendar() throws IOException {
        super();
    }

    public static void main(String[] args) throws Exception {
        new MovieCalendar().manipulatePdf(DEST);
    }

    @Override
    protected void drawMovieInfo(Screening screening, Document doc, Text press) {
        super.drawMovieInfo(screening, doc, press);
        Rectangle rect = getPosition(screening);
        Paragraph p = new Paragraph().add(screening.getMovie().getMovieTitle()).
                setFixedPosition(rect.getX(), rect.getY(), rect.getWidth()).
                setHeight(rect.getHeight()).
                setTextAlignment(TextAlignment.CENTER);
        doc.add(p);
    }
}
