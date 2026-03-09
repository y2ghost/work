package study.ywork.doc.itext.action;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TextRenderer;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;

public class MovieAnnotations3 {
    public static final String DEST = "movieAnnotations3.pdf";
    public static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";

    public static void main(String[] args) throws IOException, SQLException {
        new MovieAnnotations3().manipulatePdf();
    }

    public void manipulatePdf() throws IOException, SQLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
             Document doc = new Document(pdfDoc)) {
            for (Movie movie : SqlUtils.getMovies()) {
                Paragraph paragraph = new Paragraph(movie.getMovieTitle());
                Text text = new Text("\u00a0\u00a0");
                text.setNextRenderer(new AnnotatedTextRenderer(text, String.format(RESOURCE, movie.getImdb()),
                        String.format("img_%s.jpg", movie.getImdb()), movie.getMovieTitle()));
                paragraph.add(text);
                doc.add(paragraph);
                doc.add(ElementFactory.getDirectorList(movie));
                doc.add(ElementFactory.getCountryList(movie));
            }
        }
    }

    private static class AnnotatedTextRenderer extends TextRenderer {
        private final String fileDisplay;
        private final String filePath;
        private final String fileTitle;

        public AnnotatedTextRenderer(Text textElement, String path, String display, String title) {
            super(textElement);
            fileDisplay = display;
            filePath = path;
            fileTitle = title;
        }

        @Override
        public IRenderer getNextRenderer() {
            return new AnnotatedTextRenderer((Text) modelElement, filePath, fileDisplay, fileTitle);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfFileSpec fs = null;

            try {
                fs = PdfFileSpec.createEmbeddedFileSpec(drawContext.getDocument(), filePath, null, fileDisplay, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Rectangle rect = new Rectangle(getOccupiedAreaBBox().getLeft() + getOccupiedAreaBBox().getWidth() / 4,
                    getOccupiedAreaBBox().getBottom(), 10, 10);
            PdfFileAttachmentAnnotation annotation =
                    new PdfFileAttachmentAnnotation(rect, fs);
            annotation.setIconName(PdfName.Paperclip);
            annotation.setContents(fileTitle);
            annotation.put(PdfName.Name, new PdfString("Paperclip"));
            drawContext.getDocument().getLastPage().addAnnotation(annotation);
        }
    }
}
