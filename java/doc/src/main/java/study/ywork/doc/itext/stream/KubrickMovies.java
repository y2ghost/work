package study.ywork.doc.itext.stream;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.kernel.pdf.collection.PdfCollectionField;
import com.itextpdf.kernel.pdf.collection.PdfCollectionItem;
import com.itextpdf.kernel.pdf.collection.PdfCollectionSchema;
import com.itextpdf.kernel.pdf.collection.PdfCollectionSort;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.SqlUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class KubrickMovies {
    private static final String DEST = "kubrickMovies.pdf";
    private static final String RESOURCE_FILES = "src/main/resources/pdfs/%s.pdf";
    private static final String RESOURCE_PDFS_PREFIX = "16_09_";

    public static void main(String[] args) {
        new KubrickMovies().manipulatePdf();
    }

    public void manipulatePdf() {
        try (FileOutputStream os = new FileOutputStream(DEST)) {
            os.write(createPdf());
            os.flush();
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private PdfCollectionSchema getCollectionSchema() {
        PdfCollectionSchema schema = new PdfCollectionSchema();
        PdfCollectionField size = new PdfCollectionField("File size", PdfCollectionField.SIZE);
        size.setOrder(4);
        schema.addField("SIZE", size);

        PdfCollectionField filename = new PdfCollectionField("File name", PdfCollectionField.FILENAME);
        filename.setVisibility(false);
        schema.addField("FILE", filename);

        PdfCollectionField title = new PdfCollectionField("Movie title", PdfCollectionField.TEXT);
        title.setOrder(1);
        schema.addField("TITLE", title);

        PdfCollectionField duration = new PdfCollectionField("Duration", PdfCollectionField.NUMBER);
        duration.setOrder(2);
        schema.addField("DURATION", duration);

        PdfCollectionField year = new PdfCollectionField("Year", PdfCollectionField.NUMBER);
        year.setOrder(0);
        schema.addField("YEAR", year);
        return schema;
    }

    public byte[] createPdf() throws IOException, SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(byteOut));
             Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph("This document contains a collection of PDFs, one per Stanley Kubrick movie."));
            PdfCollection collection = new PdfCollection();
            collection.setView(PdfCollection.DETAILS);
            PdfCollectionSchema schema = getCollectionSchema();
            collection.setSchema(schema);
            PdfCollectionSort sort = new PdfCollectionSort("YEAR");
            sort.setSortOrder(false);
            collection.setSort(sort);
            collection.setInitialDocument("Eyes Wide Shut");
            pdfDoc.getCatalog().setCollection(collection);

            DatabaseConnection connection = DBUtils.getFilmDBConnection();
            java.util.List<Movie> movies = SqlUtils.getMovies(connection, 1);
            connection.close();

            for (Movie movie : movies) {
                PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(pdfDoc,
                    String.format(RESOURCE_FILES, RESOURCE_PDFS_PREFIX + movie.getImdb()),
                    movie.getTitle(),
                    String.format("kubrick_%s.pdf", movie.getImdb()),
                    null, null);

                PdfCollectionItem item = new PdfCollectionItem(schema);
                item.addItem("TITLE", movie.getMovieTitle(false));

                if (movie.getMovieTitle(true) != null) {
                    item.setPrefix("TITLE", movie.getMovieTitle(true));
                }

                item.addItem("DURATION", new PdfNumber(movie.getDuration()));
                item.addItem("YEAR", new PdfNumber(movie.getYear()));
                fs.setCollectionItem(item);
                pdfDoc.addFileAttachment(movie.getTitle(), fs);
            }
        }

        return byteOut.toByteArray();
    }
}

