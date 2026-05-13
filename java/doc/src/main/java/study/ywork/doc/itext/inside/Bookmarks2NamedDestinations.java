package study.ywork.doc.itext.inside;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.itext.action.LinkActions;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.MovieComparator;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

public class Bookmarks2NamedDestinations {
    private static final String DEST = "bookmarks2NamedDestinations_named_destinations.pdf";
    private static final String RESULT1 = "bookmarks2NamedDestinations.pdf";
    private static final String RESULT2 = "bookmarks2NamedDestinations_named_destinations.xml";
    private static final String[] EPOCH =
            {"Forties", "Fifties", "Sixties", "Seventies", "Eighties",
                    "Nineties", "Twenty-first Century"};

    public static void main(String[] args) {
        new Bookmarks2NamedDestinations().manipulatePdf();
    }

    private void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Set<Movie> movies = new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
            movies.addAll(SqlUtils.getMovies());
            int epoch = -1;
            int currentYear = 0;
            Div firstLevelParagraph = null;
            PdfOutline rootOutLine = pdfDoc.getOutlines(false);
            PdfOutline firstLevel = null;
            int firstLevelOrder = 0;
            int secondLevelOrder = 0;
            int thirdLevelOrder = 0;

            for (Movie movie : movies) {
                if (epoch < (movie.getYear() - 1940) / 10) {
                    epoch = (movie.getYear() - 1940) / 10;
                    if (null != firstLevelParagraph) {
                        doc.add(new AreaBreak());
                    }

                    firstLevelOrder++;
                    secondLevelOrder = 0;
                    String firstLevelTitle = firstLevelOrder + " " + EPOCH[epoch];
                    firstLevelParagraph = new Div().add(new Paragraph(firstLevelTitle).setFont(font).setFontSize(24).setBold());
                    doc.add(firstLevelParagraph);
                    firstLevel = rootOutLine.addOutline(firstLevelTitle);
                    firstLevel.addDestination(PdfExplicitDestination.createFit(pdfDoc.getLastPage()));

                }

                if (currentYear < movie.getYear()) {
                    currentYear = movie.getYear();
                    secondLevelOrder++;
                    thirdLevelOrder = 0;
                    String secondLevelTitle = firstLevelOrder + ". " + secondLevelOrder + " " +
                            String.format("The year %d", movie.getYear());
                    Div secondLevelParagraph = new Div().add(new Paragraph(secondLevelTitle).setFont(font).setFontSize(18));
                    PdfOutline secondLevel = firstLevel.addOutline(secondLevelTitle);
                    secondLevel.addDestination(PdfExplicitDestination.createFit(pdfDoc.getLastPage()));
                    secondLevelParagraph.add(new Paragraph(
                            String.format("Movies from the year %d:", movie.getYear())).setMarginLeft(10));
                    doc.add(secondLevelParagraph);
                }

                thirdLevelOrder++;
                String thirdLevelTitle = thirdLevelOrder + " " + movie.getMovieTitle();
                Div thirdLevelParagraph = new Div().add(new Paragraph(thirdLevelTitle).setFont(font).setFontSize(14).setMarginLeft(20));

                thirdLevelParagraph.add(new Paragraph("Duration: " + movie.getDuration()).setFont(bold).setMarginLeft(20));
                thirdLevelParagraph.add(new Paragraph("Director(s):").setFont(bold).setMarginLeft(20));
                thirdLevelParagraph.add(ElementFactory.getDirectorList(movie).setMarginLeft(20));
                thirdLevelParagraph.add(new Paragraph("Countries:").setFont(bold).setMarginLeft(20));
                thirdLevelParagraph.add(ElementFactory.getCountryList(movie).setMarginLeft(20));
                doc.add(thirdLevelParagraph);
            }
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void changePdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfDictionary root = pdfDoc.getCatalog().getPdfObject();
            PdfDictionary outlines = root.getAsDictionary(PdfName.Outlines);

            if (outlines == null) {
                return;
            }

            PdfArray pdfArray = new PdfArray();
            addKids(pdfArray, outlines.getAsDictionary(PdfName.First));

            if (pdfArray.size() == 0) {
                return;
            }

            PdfIndirectReference ref = pdfArray.makeIndirect(pdfDoc).getIndirectReference();
            PdfDictionary nameTree = new PdfDictionary();
            nameTree.put(PdfName.Names, ref);
            PdfDictionary names = new PdfDictionary();
            names.put(PdfName.Dests, nameTree);
            root.put(PdfName.Names, names);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static void addKids(PdfArray pdfArray, PdfDictionary outline) {
        while (outline != null) {
            pdfArray.add(outline.getAsString(PdfName.Title));
            pdfArray.add(outline.getAsArray(PdfName.Dest));
            addKids(pdfArray, outline.getAsDictionary(PdfName.First));
            outline = outline.getAsDictionary(PdfName.Next);
        }
    }

    protected void manipulatePdf() {
        createPdf(RESULT1);
        changePdf(RESULT1, DEST);
        LinkActions.createXml(DEST, RESULT2);
    }
}
