package study.ywork.doc.itext.pdftool;

import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InsertPages {
    private static final String DEST_TEMP = "insertPages.pdf";
    private static final String DEST = "insertPages_reordered.pdf";
    private static final String STAMP_STATIONERY = "stampStationery.pdf";
    private static final String STATIONERY_WATERMARK = "src/main/resources/pdfs/stationery_watermark.pdf";

    public static void main(String[] args) throws IOException, SQLException {
        new InsertPages().manipulatePdf();
    }

    public void manipulatePdf() throws IOException, SQLException {
        changePdf(STAMP_STATIONERY, DEST_TEMP);
        try (PdfDocument resultDoc = new PdfDocument(new PdfWriter(DEST));
             PdfDocument srcDoc = new PdfDocument(new PdfReader(DEST_TEMP))) {
            resultDoc.initializeOutlines();
            List<Integer> pageNumbers = new ArrayList<>();
            pageNumbers.add(srcDoc.getNumberOfPages() - 1);
            pageNumbers.add(srcDoc.getNumberOfPages());

            for (int i = 1; i <= srcDoc.getNumberOfPages() - 2; i++) {
                pageNumbers.add(i);
            }

            srcDoc.copyPagesTo(pageNumbers, resultDoc);
        }
    }

    public void changePdf(String src, String dest) throws SQLException, IOException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try (PdfDocument pdfDocToInsert = new PdfDocument(new PdfWriter(stream));
             Document doc = new Document(pdfDocToInsert);
             Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT country, id FROM film_country ORDER BY country");
            pdfDocToInsert.addEventHandler(PdfDocumentEvent.START_PAGE, new StationeryHandler(STATIONERY_WATERMARK));
            doc.setTopMargin(72);

            while (rs.next()) {
                doc.add(new Paragraph(rs.getString("country")).setFontSize(24));
            }
        }

        connection.close();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
             PdfDocument srcDoc = new PdfDocument(new PdfReader(new RandomAccessSourceFactory()
                     .createSource(stream.toByteArray()), new ReaderProperties()))) {
            srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), pdfDoc);
        }
    }

    public static class StationeryHandler implements IEventHandler {
        private PdfDocument stationery;

        public StationeryHandler(String src) {
            try {
                stationery = new PdfDocument(new PdfReader(src));
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfFormXObject page = null;
            try {
                page = stationery.getPage(1).copyAsFormXObject(docEvent.getDocument());
            } catch (IOException e) {
                e.printStackTrace();
            }

            new PdfCanvas(docEvent.getPage().newContentStreamBefore(),
                    docEvent.getPage().getResources(),
                    docEvent.getDocument())
                    .addXObjectAt(page, 0, 0);
        }
    }
}
