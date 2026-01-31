package study.ywork.doc.itext.event;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.MovieComparator;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

public class MovieHistoryEvent2 {
    private static final String DEST = "movieHistory2.pdf";
    private static final String[] EPOCH =
            {"Forties", "Fifties", "Sixties", "Seventies", "Eighties",
                    "Nineties", "Twenty-first Century"};
    private final PdfFont font;
    private final PdfFont bold;

    public MovieHistoryEvent2() throws IOException {
        this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    public static void main(String[] args) throws IOException {
        new MovieHistoryEvent2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            doc.setMargins(54, 36, 54, 36);
            HeaderHandler handler = new HeaderHandler();
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
            Set<Movie> movies = new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
            movies.addAll(SqlUtils.getMovies());
            int epoch = -1;
            int currentYear = 0;
            Div firstTitle = null;
            Div secondTitle = null;

            PdfOutline rootOutLine = pdfDoc.getOutlines(false);
            PdfOutline firstLevel = null;
            PdfOutline secondLevel = null;

            for (Movie movie : movies) {
                if (epoch < (movie.getYear() - 1940) / 10) {
                    epoch = (movie.getYear() - 1940) / 10;
                    if (null != firstTitle) {
                        if (null == handler.getHeader()) {
                            handler.setHeader(EPOCH[epoch - 1]);
                            doc.add(firstTitle);
                        } else {
                            handler.setHeader(EPOCH[epoch - 1]);
                            doc.add(new AreaBreak());
                            doc.add(firstTitle);
                        }
                    }

                    firstTitle = new Div().add(new Paragraph(EPOCH[epoch]).setFont(bold).setFontSize(24));
                    firstTitle.setNextRenderer(new SectionRenderer(firstTitle, 1));
                    firstTitle.setDestination(EPOCH[epoch]);
                    firstLevel = rootOutLine.addOutline(EPOCH[epoch]);
                    firstLevel.addDestination(PdfDestination.makeDestination(new PdfString(EPOCH[epoch])));
                }

                if (currentYear < movie.getYear()) {
                    currentYear = movie.getYear();
                    secondTitle = new Div().add(new Paragraph(
                            String.format("The year %d", movie.getYear())).setFont(font).setFontSize(18));
                    secondTitle.setDestination(String.valueOf(movie.getYear()));
                    secondTitle.setNextRenderer(new SectionRenderer(secondTitle, 2));
                    secondLevel = firstLevel.addOutline(String.valueOf(movie.getYear()));
                    secondLevel.addDestination(PdfDestination.makeDestination(new PdfString(String.valueOf(movie.getYear()))));
                    secondTitle.add(new Paragraph(
                            String.format("Movies from the year %d:", movie.getYear())));
                    firstTitle.add(secondTitle);

                }

                Div thirdTitle = new Div().add(new Paragraph(movie.getMovieTitle()).setFont(font).setFontSize(14).setMarginLeft(20));
                thirdTitle.setNextRenderer(new SectionRenderer(thirdTitle, 3));
                thirdTitle.setDestination(movie.getMovieTitle());
                PdfOutline thirdLevel = secondLevel.addOutline(movie.getMovieTitle());
                thirdLevel.addDestination(PdfDestination.makeDestination(new PdfString(movie.getMovieTitle())));

                thirdTitle.add(new Paragraph("Duration: " + movie.getDuration()).setFont(bold));
                thirdTitle.add(new Paragraph("Director(s):").setFont(bold));
                thirdTitle.add(ElementFactory.getDirectorList(movie));
                thirdTitle.add(new Paragraph("Countries:").setFont(bold));
                thirdTitle.add(ElementFactory.getCountryList(movie));
                secondTitle.add(thirdTitle);
            }

            handler.setHeader(EPOCH[EPOCH.length - 1]);
            doc.add(new AreaBreak());
            doc.add(firstTitle);
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected static class HeaderHandler implements IEventHandler {
        private String header;

        public void setHeader(String header) {
            this.header = header;
        }

        public String getHeader() {
            return header;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            Rectangle artBox = new Rectangle(36, 54, 523, 734);
            page.setArtBox(artBox);
            PdfDocument pdfDoc = ((PdfDocumentEvent) event).getDocument();
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
            try (Canvas canvas1 = new Canvas(canvas, artBox);
                 Canvas canvas2 = new Canvas(canvas, artBox);
                 Canvas canvas3 = new Canvas(canvas, artBox)) {
                canvas1.add(new Paragraph(header)
                        .setMargin(0)
                        .setMultipliedLeading(1)
                        .setFixedPosition(36, 800, 150));
                canvas2.add(new Paragraph("Movie History")
                        .setMargin(0)
                        .setMultipliedLeading(1)
                        .setFixedPosition(470, 800, 150));
                canvas3.add(new Paragraph(Integer.toString(pdfDoc.getPageNumber(page)))
                        .setMargin(0)
                        .setMultipliedLeading(1)
                        .setFixedPosition(285, 36, 30));
            }
        }
    }

    static class SectionRenderer extends BlockRenderer {
        private final int depth;
        private boolean drawLine = true;

        public SectionRenderer(IBlockElement modelElement, int depth) {
            super(modelElement);
            this.depth = depth;
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            Rectangle position = getOccupiedAreaBBox();

            if (drawLine) {
                drawContext.getCanvas()
                        .saveState()
                        .moveTo(position.getLeft(), depth > 1 ? position.getBottom() - 5 : position.getBottom() - 3)
                        .lineTo(position.getRight(), depth > 1 ? position.getBottom() - 5 : position.getBottom() - 3)
                        .stroke()
                        .restoreState();
            }
        }

        @Override
        public SectionRenderer getNextRenderer() {
            return new SectionRenderer((IBlockElement) modelElement, depth);
        }

        @Override
        protected AbstractRenderer createSplitRenderer(int layoutResult) {
            SectionRenderer splitRenderer = getNextRenderer();
            splitRenderer.parent = parent;
            splitRenderer.modelElement = modelElement;
            splitRenderer.occupiedArea = occupiedArea;
            splitRenderer.isLastRendererForModelElement = false;
            splitRenderer.drawLine = false;
            return splitRenderer;
        }

        @Override
        protected AbstractRenderer createOverflowRenderer(int layoutResult) {
            SectionRenderer overflowRenderer = getNextRenderer();
            overflowRenderer.parent = parent;
            overflowRenderer.modelElement = modelElement;
            overflowRenderer.properties = properties;
            return overflowRenderer;
        }
    }
}
