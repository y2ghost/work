package study.ywork.doc.itext.pdftool;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.util.SqlUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DataSheets1 extends FillDataSheet {
    private static final String DEST = "dataSheets1.pdf";

    public static void main(String[] args) throws IOException {
        new DataSheets1().manipulatePdf(DEST);
    }

    /**
     * 生成的PDF文件比较大、避免使用
     */
    @Override
    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDocResult = new PdfDocument(new PdfWriter(dest))) {
            pdfDocResult.initializeOutlines();
            addDataSheet(pdfDocResult);
        }
    }

    public void addDataSheet(PdfDocument pdfDocResult) throws IOException {
        List<Movie> movies;
        try {
            movies = SqlUtils.getMovies();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return;
        }

        for (Movie movie : movies) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(DATASHEET), new PdfWriter(baos));
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            form.setGenerateAppearance(true);
            fill(form, movie);
            form.flattenFields();

            pdfDoc.close();
            pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
            pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), pdfDocResult);
        }
    }
}
