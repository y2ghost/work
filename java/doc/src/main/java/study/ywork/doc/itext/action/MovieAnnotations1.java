package study.ywork.doc.itext.action;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;

public class MovieAnnotations1 {
    public static final String DEST = "movieAnnotations1.pdf";
    public static final String INFO = "Movie produced in %s; run length: %s";

    public static void main(String[] args) {
        new MovieAnnotations1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            for (Movie movie : SqlUtils.getMovies()) {
                Paragraph p = new Paragraph(movie.getMovieTitle());
                p.setNextRenderer(new AnnotatedParagraphRenderer(p, movie.getTitle(),
                        String.format(INFO, movie.getYear(), movie.getDuration())));
                doc.add(p);
                doc.add(ElementFactory.getDirectorList(movie));
                doc.add(ElementFactory.getCountryList(movie));
            }
        } catch (SQLException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static class AnnotatedParagraphRenderer extends ParagraphRenderer {
        private final String text;
        private final String contents;

        public AnnotatedParagraphRenderer(Paragraph modelElement, String text, String contents) {
            super(modelElement);
            this.text = text;
            this.contents = contents;
        }

        @Override
        public IRenderer getNextRenderer() {
            return new AnnotatedParagraphRenderer((Paragraph) modelElement, text, contents);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            Rectangle rect = new Rectangle(getOccupiedAreaBBox().getLeft() + getOccupiedAreaBBox().getWidth() / 4,
                    getOccupiedAreaBBox().getBottom(), 10, 10);
            PdfAnnotation textAnnot = new PdfTextAnnotation(rect)
                    .setText(new PdfString(text))
                    .setContents(new PdfString(contents));
            drawContext.getDocument().getLastPage().addAnnotation(textAnnot);
        }
    }
}
