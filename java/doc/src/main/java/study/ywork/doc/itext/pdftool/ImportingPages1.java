package study.ywork.doc.itext.pdftool;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;

public class ImportingPages1 {
    private static final String DEST = "importingPages1.pdf";
    private static final String MOVIE_TEMPLATES = "movieTemplates.pdf";

    public static void main(String[] args) throws IOException {
        new ImportingPages1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        try (PdfDocument resultDoc = new PdfDocument(new PdfWriter(dest));
             PdfDocument srcDoc = new PdfDocument(new PdfReader(MOVIE_TEMPLATES));
             Document doc = new Document(resultDoc)) {
            for (int i = 1; i <= srcDoc.getNumberOfPages(); i++) {
                PdfFormXObject header = srcDoc.getPage(i).copyAsFormXObject(resultDoc);
                Cell cell = new Cell().add(new Image(header).setWidth(UnitValue.createPercentValue(100)).setAutoScale(true));
                table.addCell(cell);
            }

            doc.add(table);
        }
    }
}
