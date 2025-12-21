package study.ywork.doc.itext.action;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoviePostersAction1 {
    private static final String DEST = "moviePostersAction1.pdf";
    private static final String IMDB = "https://imdb.com/title/tt%s/";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";

    public static void main(String[] args) {
        new MoviePostersAction1().manipulatePdf();
    }

    public void manipulatePdf() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4))) {
            PdfFormXObject xObject = new PdfFormXObject(new Rectangle(595, 84.2f));
            PdfCanvas xObjectCanvas = new PdfCanvas(xObject, pdfDoc);
            xObjectCanvas.rectangle(8, 8, 571, 60);

            for (float f = 8.25f; f < 581; f += 6.5f) {
                xObjectCanvas.roundRectangle(f, 8.5f, 6, 3, 1.5f);
                xObjectCanvas.roundRectangle(f, 72.5f, 6, 3, 1.5f);
            }

            xObjectCanvas.setFillColorGray(0.1f);
            xObjectCanvas.eoFill();
            PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.addNewPage());

            for (int i = 0; i < 10; i++) {
                pdfCanvas.addXObjectAt(xObject, 0, i * 84.2f);
            }


            PdfArray border = new PdfArray(new float[]{0, 0, 0});
            float x = 11.5f;
            float y = 769.7f;
            List<Image> images = new ArrayList<>();

            for (Movie movie : SqlUtils.getMovies()) {
                Image img = new Image(ImageDataFactory.create(String.format(RESOURCE, movie.getImdb())));
                img.scaleToFit(1000, 60);
                img.setFixedPosition(x + (45 - img.getImageScaledWidth()) / 2, y);
                PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(x + (45 - img.getImageScaledWidth()) / 2, y,
                        img.getImageScaledWidth(), img.getImageScaledHeight()));
                linkAnnotation.setBorder(border);
                linkAnnotation.setAction(PdfAction.createURI(String.format(IMDB, movie.getImdb())));
                pdfDoc.getLastPage().addAnnotation(linkAnnotation);
                images.add(img);
                x += 48;

                if (x > 578) {
                    x = 11.5f;
                    y -= 84.2f;
                }
            }

            try (Canvas canvas = new Canvas(pdfCanvas, pdfDoc.getLastPage().getPageSize())) {
                images.forEach(canvas::add);
            }
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
