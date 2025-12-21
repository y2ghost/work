package study.ywork.doc.itext.inside;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.itext.block.MovieParagraphs1;
import study.ywork.doc.util.SqlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PageLayoutExample extends MovieParagraphs1 {
    private static final String[] RESULT = new String[]{
            "pageLayoutSingle.pdf",
            "pageLayoutColumn.pdf",
            "pageLayoutColumns_l.pdf",
            "pageLayoutColumns_r.pdf",
            "pageLayoutPages_l.pdf",
            "pageLayoutPages_r.pdf"
    };
    private static final String DEST = RESULT[0];

    public static void main(String[] args) {
        new PageLayoutExample().manipulatePdf(DEST);
    }

    private void createPdf(String dest, PdfName pageLayoutMode) {
        try {
            createFonts();
            try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(PdfVersion.PDF_1_5)));
                 Document doc = new Document(pdfDoc)) {
                pdfDoc.getCatalog().setPageLayout(pageLayoutMode);
                List<Movie> movies = SqlUtils.getMovies();

                for (Movie movie : movies) {
                    Paragraph p = createMovieInformation(movie);
                    p.setTextAlignment(TextAlignment.JUSTIFIED);
                    p.setMarginLeft(18);
                    p.setFirstLineIndent(-18);
                    doc.add(p);
                }
            }
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public void manipulatePdf(String dest) {
        createPdf(RESULT[0], PdfName.SinglePage);
        createPdf(RESULT[1], PdfName.OneColumn);
        createPdf(RESULT[2], PdfName.TwoColumnLeft);
        createPdf(RESULT[3], PdfName.TwoColumnRight);
        createPdf(RESULT[4], PdfName.TwoPageLeft);
        createPdf(RESULT[5], PdfName.TwoPageRight);
    }
}
