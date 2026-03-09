package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrintDocumentMetaData {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                PrintDocumentMetaData meta = new PrintDocumentMetaData();
                meta.printMetadata(document);
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + PrintDocumentMetaData.class.getName() + " <input-pdf>");
    }

    public void printMetadata(PDDocument document) throws IOException {
        PDDocumentInformation info = document.getDocumentInformation();
        PDDocumentCatalog cat = document.getDocumentCatalog();
        PDMetadata metadata = cat.getMetadata();
        System.out.println("Page Count=" + document.getNumberOfPages());
        System.out.println("Title=" + info.getTitle());
        System.out.println("Author=" + info.getAuthor());
        System.out.println("Subject=" + info.getSubject());
        System.out.println("Keywords=" + info.getKeywords());
        System.out.println("Creator=" + info.getCreator());
        System.out.println("Producer=" + info.getProducer());
        System.out.println("Creation Date=" + formatDate(info.getCreationDate()));
        System.out.println("Modification Date=" + formatDate(info.getModificationDate()));
        System.out.println("Trapped=" + info.getTrapped());
        if (metadata != null) {
            String string = new String(metadata.toByteArray(), StandardCharsets.ISO_8859_1);
            System.out.println("Metadata=" + string);
        }
    }

    private String formatDate(Calendar date) {
        String retval = null;
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat();
            retval = formatter.format(date.getTime());
        }

        return retval;
    }
}
