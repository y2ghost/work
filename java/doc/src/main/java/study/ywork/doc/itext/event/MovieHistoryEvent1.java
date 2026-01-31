package study.ywork.doc.itext.event;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MovieHistoryEvent1 {
    private static final String DEST = "movieHistoryEvent1.pdf";
    private List<IBlockElement> titles = new ArrayList<>();
    private static final String[] EPOCH =
            {"Forties", "Fifties", "Sixties", "Seventies", "Eighties",
                    "Nineties", "Twenty-first Century"};
    private final PdfFont font;
    private final PdfFont bold;

    public MovieHistoryEvent1() throws IOException {
        this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    public static void main(String[] args) throws IOException {
        new MovieHistoryEvent1().manipulatePdf();
    }

    public void manipulatePdf() {
        int toc = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
             Document doc = new Document(pdfDoc)) {
            Set<Movie> movies = new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
            movies.addAll(SqlUtils.getMovies());
            int epoch = -1;
            int currentYear = 0;
            Div firstLevelParagraph = null;
            Div secondLevelParagraph = null;

            PdfOutline rootOutLine = pdfDoc.getOutlines(false);
            PdfOutline firstLevel = null;
            PdfOutline secondLevel = null;
            int firstLevelOrder = 0;
            int secondLevelOrder = 0;
            int thirdLevelOrder = 0;

            for (Movie movie : movies) {
                if (epoch < (movie.getYear() - 1940) / 10) {
                    epoch = (movie.getYear() - 1940) / 10;
                    if (null != firstLevelParagraph) {
                        doc.add(firstLevelParagraph);
                        doc.add(new AreaBreak());
                    }

                    firstLevelOrder++;
                    secondLevelOrder = 0;
                    String firstLevelTitle = firstLevelOrder + " " + EPOCH[epoch];
                    firstLevelParagraph = new Div().add(new Paragraph(firstLevelTitle).setFont(bold).setFontSize(24));
                    firstLevelParagraph.setNextRenderer(new SectionRenderer(firstLevelParagraph, 1));
                    firstLevelParagraph.setDestination(firstLevelTitle);
                    firstLevel = rootOutLine.addOutline(firstLevelTitle);
                    firstLevel.addDestination(PdfDestination.makeDestination(new PdfString(firstLevelTitle)));
                    titles.add(new Paragraph(firstLevelTitle).setFontSize(10));
                }

                if (currentYear < movie.getYear()) {
                    currentYear = movie.getYear();
                    secondLevelOrder++;
                    thirdLevelOrder = 0;
                    String secondLevelTitle = firstLevelOrder + ". " + secondLevelOrder + " " +
                            String.format("The year %d", movie.getYear());
                    secondLevelParagraph = new Div().add(new Paragraph(secondLevelTitle).setFont(font).setFontSize(18));
                    secondLevelParagraph.setDestination(secondLevelTitle);
                    secondLevelParagraph.setNextRenderer(new SectionRenderer(secondLevelParagraph, 2));
                    secondLevel = firstLevel.addOutline(secondLevelTitle);
                    secondLevel.addDestination(PdfDestination.makeDestination(new PdfString(secondLevelTitle)));

                    secondLevelParagraph.add(new Paragraph(
                            String.format("Movies from the year %d:", movie.getYear())).setMarginLeft(10));

                    titles.add(new Paragraph(secondLevelTitle)
                            .setFont(font)
                            .setFontSize(10)
                            .setMarginLeft(10));

                    firstLevelParagraph.add(secondLevelParagraph);

                }

                thirdLevelOrder++;
                String thirdLevelTitle = thirdLevelOrder + " " + movie.getMovieTitle();
                Div thirdLevelParagraph = new Div().add(new Paragraph(thirdLevelTitle).setFont(font).setFontSize(14).setMarginLeft(20));
                thirdLevelParagraph.setNextRenderer(new SectionRenderer(thirdLevelParagraph, 3));
                thirdLevelParagraph.setDestination(thirdLevelTitle);
                PdfOutline thirdLevel = secondLevel.addOutline(thirdLevelTitle);
                thirdLevel.addDestination(PdfDestination.makeDestination(new PdfString(thirdLevelTitle)));

                thirdLevelParagraph.add(new Paragraph("Duration: " + movie.getDuration()).setFont(bold).setMarginLeft(20));
                thirdLevelParagraph.add(new Paragraph("Director(s):").setFont(bold).setMarginLeft(20));
                thirdLevelParagraph.add(ElementFactory.getDirectorList(movie).setMarginLeft(20));
                thirdLevelParagraph.add(new Paragraph("Countries:").setFont(bold).setMarginLeft(20));
                thirdLevelParagraph.add(ElementFactory.getCountryList(movie).setMarginLeft(20));
                titles.add(new Paragraph(thirdLevelTitle)
                        .setFont(font)
                        .setMarginLeft(20)
                        .setFontSize(10));
                secondLevelParagraph.add(thirdLevelParagraph);
            }

            doc.add(firstLevelParagraph);
            doc.add(new AreaBreak());
            toc = pdfDoc.getNumberOfPages();

            for (IBlockElement p : titles) {
                doc.add(p);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        try (PdfDocument srcDoc = new PdfDocument(new PdfReader(new RandomAccessSourceFactory().createSource(baos.toByteArray()),
                new ReaderProperties())); PdfDocument resultDoc = new PdfDocument(new PdfWriter(DEST))) {
            int total = srcDoc.getNumberOfPages();
            int[] order = new int[total];

            for (int i = 0; i < total; i++) {
                order[i] = i + toc;
                if (order[i] > total)
                    order[i] -= total;
            }

            resultDoc.initializeOutlines();
            List<Integer> pages = new ArrayList<>();
            for (int i : order) {
                pages.add(i);
            }

            srcDoc.getOutlines(false);
            srcDoc.copyPagesTo(pages, resultDoc);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    class SectionRenderer extends BlockRenderer {
        protected int depth;
        protected boolean drawLine = true;

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
