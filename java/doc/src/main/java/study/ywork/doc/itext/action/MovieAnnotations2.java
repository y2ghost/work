package study.ywork.doc.itext.action;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
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

public class MovieAnnotations2 {
    private static final String DEST = "movieAnnotations2.pdf";
    private static final String INFO = "Movie produced in %s; run length: %s";

    public static void main(String[] args) throws IOException, SQLException {
        new MovieAnnotations2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            for (Movie movie : SqlUtils.getMovies()) {
                Paragraph p = new Paragraph(movie.getMovieTitle());
                Text text = new Text("\u00a0");
                text.setNextRenderer(new AnnotatedTextRenderer(text, movie.getMovieTitle(),
                        String.format(INFO, movie.getYear(), movie.getDuration())));
                p.add(text);
                doc.add(p);
                doc.add(ElementFactory.getDirectorList(movie));
                doc.add(ElementFactory.getCountryList(movie));
            }
        }
    }

    private static class AnnotatedTextRenderer extends TextRenderer {
        private final String contents;
        private final String title;

        public AnnotatedTextRenderer(Text textElement, String title, String contents) {
            super(textElement);
            this.title = title;
            this.contents = contents;
        }

        @Override
        public IRenderer getNextRenderer() {
            return new AnnotatedTextRenderer((Text) modelElement, title, contents);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            Rectangle rect = new Rectangle(getOccupiedAreaBBox().getLeft() + getOccupiedAreaBBox().getWidth() / 4,
                    getOccupiedAreaBBox().getBottom(), 10, 10);
            PdfAnnotation annotation = new PdfTextAnnotation(rect)
                    .setOpen(false)
                    .setTitle(new PdfString(title))
                    .setContents(new PdfString(contents))
                    .setName(new PdfString("Comment"));
            drawContext.getDocument().getLastPage().addAnnotation(annotation);
        }
    }
}
