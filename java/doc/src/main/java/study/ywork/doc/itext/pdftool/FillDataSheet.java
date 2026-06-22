package study.ywork.doc.itext.pdftool;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.util.SqlUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FillDataSheet {
    protected static final String DATASHEET = "src/main/resources/pdfs/datasheet.pdf";
    private static final String DEST = "fillDataSheet.pdf";

    public static void main(String[] args) throws IOException {
        new FillDataSheet().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        List<Movie> movies;
        try {
            movies = SqlUtils.getMovies();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return;
        }

        try (PdfDocument pdfDocResult = new PdfDocument(new PdfWriter(dest))) {
            pdfDocResult.initializeOutlines();
            PdfPageFormCopier formsCopier = new PdfPageFormCopier();

            for (Movie movie : movies) {
                copyPages(movie, pdfDocResult, formsCopier);
            }
        }
    }

    private void copyPages(Movie movie, PdfDocument pdfDocResult, PdfPageFormCopier formsCopier) throws IOException {
        if (movie.getYear() < 2007) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(DATASHEET), new PdfWriter(baos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.setGenerateAppearance(true);
        fill(form, movie);

        if (movie.getYear() == 2007) {
            form.flattenFields();
        }

        pdfDoc.close();
        pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
        PdfAcroForm newForm = PdfAcroForm.getAcroForm(pdfDoc, false);

        if (newForm != null) {
            for (PdfFormField field : newForm.getFormFields().values()) {
                if (field.getFieldName() != null) {
                    field.setFieldName(movie.getImdb() + field.getFieldName().toUnicodeString());
                }
            }
        }

        pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), pdfDocResult, formsCopier);
    }

    protected static void fill(PdfAcroForm form, Movie movie) {
        form.getField("title").setValue(movie.getMovieTitle());
        form.getField("director").setValue(getDirectors(movie));
        form.getField("year").setValue(String.valueOf(movie.getYear()));
        form.getField("duration").setValue(String.valueOf(movie.getDuration()));
        form.getField("category").setValue(movie.getEntry().getCategory().getKeyword());

        for (Screening screening : movie.getEntry().getScreenings()) {
            form.getField(screening.getLocation().replace('.', '_')).setValue("Yes");
        }
    }

    private static String getDirectors(Movie movie) {
        List<Director> directors = movie.getDirectors();
        StringBuilder buf = new StringBuilder();

        for (Director director : directors) {
            buf.append(director.getGivenName());
            buf.append(' ');
            buf.append(director.getName());
            buf.append(',');
            buf.append(' ');
        }

        int i = buf.length();
        if (i > 0) {
            buf.delete(i - 2, i);
        }

        return buf.toString();
    }
}
