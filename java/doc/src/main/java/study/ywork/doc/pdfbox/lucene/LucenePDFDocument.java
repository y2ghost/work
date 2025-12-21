package study.ywork.doc.pdfbox.lucene;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.util.Calendar;
import java.util.Date;

public class LucenePDFDocument {
    private static final char FILE_SEPARATOR = FileSystems.getDefault().getSeparator().charAt(0);
    private static final DateTools.Resolution DATE_TIME_RES = DateTools.Resolution.SECOND;
    private PDFTextStripper stripper = null;
    public static final FieldType TYPE_STORED_NOT_INDEXED = new FieldType();

    static {
        TYPE_STORED_NOT_INDEXED.setIndexOptions(IndexOptions.NONE);
        TYPE_STORED_NOT_INDEXED.setStored(true);
        TYPE_STORED_NOT_INDEXED.setTokenized(true);
        TYPE_STORED_NOT_INDEXED.freeze();
    }

    public LucenePDFDocument() {
        // NOOP
    }

    public void setTextStripper(PDFTextStripper aStripper) {
        stripper = aStripper;
    }

    private static String timeToString(long time) {
        return DateTools.timeToString(time, DATE_TIME_RES);
    }

    private void addKeywordField(Document document, String name, String value) {
        if (value != null) {
            document.add(new StringField(name, value, Field.Store.YES));
        }
    }

    private void addTextField(Document document, String name, Reader value) {
        if (value != null) {
            document.add(new TextField(name, value));
        }
    }

    private void addTextField(Document document, String name, String value) {
        if (value != null) {
            document.add(new TextField(name, value, Field.Store.YES));
        }
    }

    private void addTextField(Document document, String name, Date value) {
        if (value != null) {
            addTextField(document, name, DateTools.dateToString(value, DATE_TIME_RES));
        }
    }

    private void addTextField(Document document, String name, Calendar value) {
        if (value != null) {
            addTextField(document, name, value.getTime());
        }
    }

    private static void addUnindexedField(Document document, String name, String value) {
        if (value != null) {
            document.add(new Field(name, value, TYPE_STORED_NOT_INDEXED));
        }
    }

    private void addUnstoredKeywordField(Document document, String name, String value) {
        if (value != null) {
            document.add(new Field(name, value, TextField.TYPE_NOT_STORED));
        }
    }

    public Document convertDocument(File file) throws IOException {
        Document document = new Document();
        // 添加不可搜索字段
        addUnindexedField(document, "path", file.getPath());
        addUnindexedField(document, "url", file.getPath().replace(FILE_SEPARATOR, '/'));
        // 关键字可以搜索
        addKeywordField(document, "modified", timeToString(file.lastModified()));
        String uid = createUID(file);
        addUnstoredKeywordField(document, "uid", uid);
        addContent(document, new RandomAccessReadBufferedFile(file), file.getPath());
        return document;
    }

    public Document convertDocument(URL url) throws IOException {
        Document document = new Document();
        URLConnection connection = url.openConnection();
        connection.connect();
        addUnindexedField(document, "url", url.toExternalForm());
        addKeywordField(document, "modified", timeToString(connection.getLastModified()));
        String uid = createUID(url, connection.getLastModified());
        addUnstoredKeywordField(document, "uid", uid);
        addContent(document,
                RandomAccessReadBuffer.createBufferFromStream(connection.getInputStream()),
                url.toExternalForm());
        return document;
    }

    public static Document getDocument(File file) throws IOException {
        LucenePDFDocument converter = new LucenePDFDocument();
        return converter.convertDocument(file);
    }

    public static Document getDocument(URL url) throws IOException {
        LucenePDFDocument converter = new LucenePDFDocument();
        return converter.convertDocument(url);
    }

    private void addContent(Document document, RandomAccessRead source, String documentLocation)
            throws IOException {
        try (PDDocument pdfDocument = Loader.loadPDF(source)) {
            StringWriter writer = new StringWriter();
            if (stripper == null) {
                stripper = new PDFTextStripper();
            }

            stripper.writeText(pdfDocument, writer);
            String contents = writer.getBuffer().toString();
            StringReader reader = new StringReader(contents);
            addTextField(document, "contents", reader);

            PDDocumentInformation info = pdfDocument.getDocumentInformation();
            if (info != null) {
                addTextField(document, "Author", info.getAuthor());
                addTextField(document, "CreationDate", info.getCreationDate());
                addTextField(document, "Creator", info.getCreator());
                addTextField(document, "Keywords", info.getKeywords());
                addTextField(document, "ModificationDate", info.getModificationDate());
                addTextField(document, "Producer", info.getProducer());
                addTextField(document, "Subject", info.getSubject());
                addTextField(document, "Title", info.getTitle());
                addTextField(document, "Trapped", info.getTrapped());
            }
            int summarySize = Math.min(contents.length(), 500);
            String summary = contents.substring(0, summarySize);
            addUnindexedField(document, "summary", summary);
        } catch (InvalidPasswordException e) {
            throw new IOException("Error: The document(" + documentLocation + ") is encrypted and will not be indexed.", e);
        }
    }

    public static String createUID(URL url, long time) {
        return url.toExternalForm().replace(FILE_SEPARATOR, '\u0000') + "\u0000" + timeToString(time);
    }

    public static String createUID(File file) {
        return file.getPath().replace(FILE_SEPARATOR, '\u0000') + "\u0000" + timeToString(file.lastModified());
    }
}
