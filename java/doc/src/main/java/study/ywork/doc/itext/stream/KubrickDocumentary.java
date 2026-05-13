package study.ywork.doc.itext.stream;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.xmp.impl.Utils;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.SqlUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

public class KubrickDocumentary {
    private static final String FILENAME = "kubrickDocumentary.pdf";
    private static final String PATH = "kubOut%s";
    private static final String DEST = String.format(PATH, FILENAME);

    public static void main(String[] args) {
        new KubrickDocumentary().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try {
            try (FileOutputStream os = new FileOutputStream(dest)) {
                os.write(createPdf());
                os.flush();
            }
            extractDocLevelAttachments(dest);
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private byte[] createPdf() throws IOException, SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        java.util.List<Movie> movies = SqlUtils.getMovies(connection, 1);
        connection.close();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
             Document doc = new Document(pdfDoc);
             ByteArrayOutputStream txt = new ByteArrayOutputStream();
             PrintStream out = new PrintStream(txt)) {
            doc.add(new Paragraph(
                "'Stanley Kubrick: A Life in Pictures' is a documentary about Stanley Kubrick and his films:"));
            out.print("<movies>\n");
            List list = new List();
            list.setSymbolIndent(20);

            for (Movie movie : movies) {
                out.print("<movie>%n");
                out.printf("<title>%s</title>%n", Utils.escapeXML(movie.getMovieTitle(), true, true));
                out.printf("<year>%s</year>%n", movie.getYear());
                out.printf("<duration>%s</duration>%n", movie.getDuration());
                out.print("</movie>%n");
                ListItem item = new ListItem(movie.getMovieTitle());
                list.add(item);
            }

            doc.add(list);
            out.print("</movies>");
            out.flush();
            pdfDoc.addFileAttachment("kubrick", PdfFileSpec.createEmbeddedFileSpec(pdfDoc,
                txt.toByteArray(), null, "kubrick.xml",
                null, null, null));
        }

        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }

    public void extractDocLevelAttachments(String src) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            PdfDictionary root = pdfDoc.getCatalog().getPdfObject();
            PdfDictionary documentNames = root.getAsDictionary(PdfName.Names);
            PdfDictionary embeddedFiles = documentNames.getAsDictionary(PdfName.EmbeddedFiles);
            PdfArray fileSpecs = embeddedFiles.getAsArray(PdfName.Names);

            for (int i = 0; i < fileSpecs.size(); i += 2) {
                fileSpecs.getAsString(i);
                PdfDictionary fileSpec = fileSpecs.getAsDictionary(i + 1);
                PdfDictionary refs = fileSpec.getAsDictionary(PdfName.EF);
                for (PdfName key : refs.keySet()) {
                    try (FileOutputStream fos = new FileOutputStream(String.format(PATH,
                        fileSpec.getAsString(key).toString()))) {
                        PdfStream stream = refs.getAsStream(key);
                        fos.write(stream.getBytes());
                        fos.flush();
                    }
                }
            }
        }
    }
}
