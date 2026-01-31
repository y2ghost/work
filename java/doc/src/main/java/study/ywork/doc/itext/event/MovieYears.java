package study.ywork.doc.itext.event;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LinkRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.layout.renderer.TextRenderer;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.MovieComparator;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MovieYears {
    private static final String DEST = "movieYears.pdf";

    public static void main(String[] args) {
        new MovieYears().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, new PageSize(PageSize.A4).rotate())) {
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD, PdfEncodings.WINANSI);
            PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE, PdfEncodings.WINANSI);
            Map<String, Integer> years = new TreeMap<>();
            Set<Movie> movies = new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
            movies.addAll(SqlUtils.getMovies());
            for (Movie movie : movies) {
                Paragraph p = new Paragraph().setFixedLeading(22);
                p.setNextRenderer(new LinedParagraphRenderer(p));
                Text text = new Text(String.format("%d ", movie.getYear())).setFont(bold);
                text.setNextRenderer(new StripTextRenderer(text));
                p.add(text);
                text = new Text(movie.getMovieTitle());
                p.add(text);
                text = new Text(String.format(" (%d minutes)  ", movie.getDuration())).setFont(italic);
                p.add(text);
                text = new Link("IMDB", PdfAction.createURI("http://www.imdb.com/title/tt" + movie.getImdb()))
                        .setFont(bold).setFontColor(ColorConstants.WHITE);
                text.setNextRenderer(new EllipseTextRenderer((Link) text));
                p.add(text);
                doc.add(p);
                Integer count = years.get(String.valueOf(movie.getYear()));

                if (count == null) {
                    years.put(String.valueOf(movie.getYear()), 1);
                } else {
                    years.put(String.valueOf(movie.getYear()), count + 1);
                }
            }
            doc.add(new AreaBreak());

            for (Map.Entry<String, Integer> entry : years.entrySet()) {
                Paragraph p = new Paragraph(String.format("%s: %d movie(s)", entry.getKey(), entry.getValue()));
                doc.add(p);
            }
        } catch (SQLException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private class StripTextRenderer extends TextRenderer {
        public StripTextRenderer(Text textElement) {
            super(textElement);
        }

        @Override
        public IRenderer getNextRenderer() {
            return new StripTextRenderer((Text) modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            Rectangle rect = getOccupiedAreaBBox();
            PdfCanvas canvas = drawContext.getCanvas();
            canvas.rectangle(rect.getLeft() - 1, rect.getBottom() - 5f,
                    rect.getWidth(), rect.getHeight() + 8);
            canvas.rectangle(rect.getLeft(), rect.getBottom() - 2,
                    rect.getWidth() - 2, rect.getHeight() + 2);
            float y1 = rect.getTop() + 0.5f;
            float y2 = rect.getBottom() - 4;
            for (float f = rect.getLeft(); f < rect.getRight() - 4; f += 5) {
                canvas.rectangle(f, y1, 4f, 1.5f);
                canvas.rectangle(f, y2, 4f, 1.5f);
            }
            canvas.eoFill();
            super.draw(drawContext);
        }
    }

    private class EllipseTextRenderer extends LinkRenderer {
        public EllipseTextRenderer(Link textElement) {
            super(textElement);
            setProperty(Property.FONT_COLOR, new TransparentColor(ColorConstants.WHITE));
        }

        @Override
        public IRenderer getNextRenderer() {
            return new EllipseTextRenderer((Link) modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            Rectangle rect = getOccupiedAreaBBox();
            PdfCanvas canvas = drawContext.getCanvas();
            canvas.saveState();
            canvas.setFillColor(new DeviceRgb(0x00, 0x00, 0xFF));
            canvas.ellipse(rect.getLeft() - 3f, rect.getBottom() - 5f,
                    rect.getRight() + 3f, rect.getTop() + 3f);
            canvas.fill();
            canvas.restoreState();
            super.draw(drawContext);
        }
    }

    private class LinedParagraphRenderer extends ParagraphRenderer {
        public LinedParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }

        @Override
        public IRenderer getNextRenderer() {
            return new LinedParagraphRenderer((Paragraph) modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            Rectangle rect = getOccupiedAreaBBox();
            PdfCanvas canvas = drawContext.getCanvas();
            canvas.moveTo(rect.getLeft(), rect.getTop() + 2);
            canvas.lineTo(rect.getRight(), rect.getTop() + 2);
            canvas.moveTo(rect.getLeft(), rect.getBottom() - 2);
            canvas.lineTo(rect.getRight(), rect.getBottom() - 2);
            canvas.stroke();
        }
    }
}
