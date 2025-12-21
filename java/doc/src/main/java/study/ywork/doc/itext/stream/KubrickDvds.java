package study.ywork.doc.itext.stream;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ListItemRenderer;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;
import study.ywork.doc.util.SqlUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

public class KubrickDvds {
    private static final String DEST = "kubrickDvds.pdf";
    private static final String RESOURCE = "src/main/resources/images/posters/%s.jpg";
    private static final String PATH = "kubOut%s";

    public static void main(String[] args) {
        new KubrickDvds().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        try {
            try (FileOutputStream os = new FileOutputStream(dest)) {
                os.write(createPdf());
                os.flush();
            }
            extractAttachments(dest);
        } catch (IOException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void extractAttachments(String src) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfArray array = pdfDoc.getPage(i).getPdfObject().getAsArray(PdfName.Annots);
                if (array == null) {
                    continue;
                }

                for (int j = 0; j < array.size(); j++) {
                    PdfDictionary annot = array.getAsDictionary(j);
                    if (PdfName.FileAttachment.equals(annot.getAsName(PdfName.Subtype))) {
                        PdfDictionary fs = annot.getAsDictionary(PdfName.FS);
                        PdfDictionary refs = fs.getAsDictionary(PdfName.EF);
                        for (PdfName name : refs.keySet()) {
                            try (FileOutputStream fos = new FileOutputStream(String.format(PATH, fs.getAsString(name).toString()))) {
                                fos.write(refs.getAsStream(name).getBytes());
                                fos.flush();
                            }
                        }
                    }
                }
            }
        }
    }

    public byte[] createPdf() throws IOException, SQLException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
             Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph("This is a list of Kubrick movies available in DVD stores."));
            DatabaseConnection connection = DBUtils.getFilmDBConnection();
            Set<Movie> movies = new TreeSet<>();
            movies.addAll(SqlUtils.getMovies(connection, 1));
            movies.addAll(SqlUtils.getMovies(connection, 4));
            connection.close();
            List list = new List();

            for (Movie movie : movies) {
                ListItem item = new ListItem(movie.getMovieTitle(false));
                item.setNextRenderer(new AnnotatedListItemRenderer(item,
                    String.format(RESOURCE, movie.getImdb()),
                    String.format("%s.jpg", movie.getImdb()),
                    movie.getMovieTitle(false)));
                list.add(item);
            }
            doc.add(list);

        }

        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }

    private static class AnnotatedListItemRenderer extends ListItemRenderer {
        private final String fileDisplay;
        private final String filePath;
        private final String fileTitle;

        public AnnotatedListItemRenderer(ListItem modelElement, String path, String display, String title) {
            super(modelElement);
            fileDisplay = display;
            filePath = path;
            fileTitle = title;
        }

        @Override
        public IRenderer getNextRenderer() {
            return new AnnotatedListItemRenderer((ListItem) modelElement, filePath, fileDisplay, fileTitle);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfFileSpec fs;

            try {
                fs = PdfFileSpec.createEmbeddedFileSpec(drawContext.getDocument(),
                    filePath, null, fileDisplay, null, null);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                return;
            }

            Rectangle rect = new Rectangle(getOccupiedAreaBBox().getRight(),
                getOccupiedAreaBBox().getBottom(), 10, 10);
            PdfFileAttachmentAnnotation annotation = new PdfFileAttachmentAnnotation(rect, fs);
            annotation.setIconName(PdfName.Paperclip);
            annotation.setContents(fileTitle);
            annotation.put(PdfName.Name, new PdfString("Paperclip"));
            drawContext.getDocument().getLastPage().addAnnotation(annotation);
        }
    }
}
