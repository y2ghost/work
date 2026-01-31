package study.ywork.doc.itext.position;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.SqlUtils;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

public class MoviePosters {
    private static final String DEST = "moviePosters.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";

    public static void main(String[] args) {
        new MoviePosters().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document document = new Document(pdfDoc)) {
            PdfPage page = pdfDoc.addNewPage();
            PdfFormXObject xObj = new PdfFormXObject(new Rectangle(8, 8, 579, 68));
            PdfCanvas celluloid = new PdfCanvas(xObj, pdfDoc);
            celluloid.rectangle(8, 8, 579, 68);

            for (float f = 8.25f; f < 581; f += 6.5f) {
                celluloid.roundRectangle(f, 8.5f, 6, 3, 1.5f)
                    .roundRectangle(f, 72.5f, 6, 3, 1.5f);
            }

            celluloid.setFillColor(new DeviceGray(0.1f)).eoFill();
            celluloid.release();
            xObj.flush();
            PdfCanvas canvas = new PdfCanvas(page);

            for (int i = 0; i < 10; i++) {
                canvas.addXObjectAt(xObj, 8, i * 84.2f + 8);
            }

            canvas.release();
            canvas = new PdfCanvas(pdfDoc.addNewPage());

            for (int i = 0; i < 10; i++) {
                canvas.addXObjectAt(xObj, 8, i * 84.2f + 8);
            }

            List<Movie> movies = SqlUtils.getMovies();
            float x = 11.5f;
            float y = 769.7f;

            for (Movie movie : movies) {
                ImageData image = ImageDataFactory.create(String.format(RESOURCE, movie.getImdb()));
                PdfImageXObject img = new PdfImageXObject(image);
                float scaleY = 60 / img.getHeight();
                canvas.addImageWithTransformationMatrix(image, img.getWidth() * scaleY, 0, 0, 60, x + (45 - image.getWidth() * scaleY) / 2, y, false);
                x += 48;
                if (x > 578) {
                    x = 11.5f;
                    y -= 84.2f;
                }
            }

            canvas.release();
            canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.addXObjectWithTransformationMatrix(xObj, 0.8f, 0, 0.35f, 0.65f, 0, 600);
            Image image = new Image(xObj, 0, 480).setPageNumber(3);
            document.add(image);
            image.setRotationAngle(Math.PI / 6).
                scale(.8f, .8f).
                setFixedPosition(3, 30, 500);
            document.add(image);
            image.setRotationAngle(Math.PI / 2).
                setFixedPosition(3, 200, 300);
            document.add(image);
        } catch (MalformedURLException | SQLException | FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
