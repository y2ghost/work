package study.ywork.doc.itext.block;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.ElementFactory;
import study.ywork.doc.util.MovieComparator;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MovieHistory {
    private static final String DEST = "movieHistory.pdf";
    private final List<IBlockElement> titles = new ArrayList<>();
    private static final String[] EPOCH =
            {"Forties", "Fifties", "Sixties", "Seventies", "Eighties",
                    "Nineties", "Twenty-first Century"};
    private final PdfFont font;
    private final PdfFont bold;

    public MovieHistory() throws IOException {
        this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        this.bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new MovieHistory().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        Set<Movie> movies = new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
        movies.addAll(SqlUtils.getMovies());

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfOutline rootOutLine = pdfDoc.getOutlines(false);
            int epoch = -1;
            int currentYear = 0;
            Div firstLevelParagraph = null;
            Div secondLevelParagraph = null;

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
        } catch (NullPointerException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
