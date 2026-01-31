package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.DomXmpParser;
import org.apache.xmpbox.xml.XmpParsingException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public final class ExtractMetadata {
    private ExtractMetadata() {
    }

    public static void main(String[] args) throws IOException, XmpParsingException, BadFieldValueException {
        if (args.length != 1) {
            usage();
            System.exit(1);
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                PDDocumentCatalog catalog = document.getDocumentCatalog();
                PDMetadata meta = catalog.getMetadata();
                if (meta != null) {
                    DomXmpParser xmpParser = new DomXmpParser();
                    try {
                        XMPMetadata metadata = xmpParser.parse(meta.toByteArray());
                        showDublinCoreSchema(metadata);
                        showAdobePDFSchema(metadata);
                        showXMPBasicSchema(metadata);
                    } catch (XmpParsingException e) {
                        System.err.println("An error occurred when parsing the metadata: "
                                + e.getMessage());
                    }
                } else {
                    // 没有找到任何metadata信息，使用文档信息替代
                    PDDocumentInformation information = document.getDocumentInformation();
                    if (information != null) {
                        showDocumentInformation(information);
                    }
                }
            }
        }
    }

    private static void showXMPBasicSchema(XMPMetadata metadata) {
        XMPBasicSchema basic = metadata.getXMPBasicSchema();
        if (basic != null) {
            display("Create Date:", basic.getCreateDate());
            display("Modify Date:", basic.getModifyDate());
            display("Creator Tool:", basic.getCreatorTool());
        }
    }

    private static void showAdobePDFSchema(XMPMetadata metadata) {
        AdobePDFSchema pdf = metadata.getAdobePDFSchema();
        if (pdf != null) {
            display("Keywords:", pdf.getKeywords());
            display("PDF Version:", pdf.getPDFVersion());
            display("PDF Producer:", pdf.getProducer());
        }
    }

    private static void showDublinCoreSchema(XMPMetadata metadata) throws BadFieldValueException {
        DublinCoreSchema dc = metadata.getDublinCoreSchema();
        if (dc != null) {
            display("Title:", dc.getTitle());
            display("Description:", dc.getDescription());
            listString("Creators: ", dc.getCreators());
            listCalendar("Dates:", dc.getDates());
            listString("Subjects:", dc.getSubjects());
        }
    }

    private static void showDocumentInformation(PDDocumentInformation information) {
        display("Title:", information.getTitle());
        display("Subject:", information.getSubject());
        display("Author:", information.getAuthor());
        display("Creator:", information.getCreator());
        display("Producer:", information.getProducer());
    }

    private static void listString(String title, List<String> list) {
        if (list == null) {
            return;
        }
        System.out.println(title);
        for (String string : list) {
            System.out.println("  " + string);
        }
    }

    private static void listCalendar(String title, List<Calendar> list) {
        if (list == null) {
            return;
        }
        System.out.println(title);
        for (Calendar calendar : list) {
            System.out.println("  " + format(calendar));
        }
    }

    private static String format(Object o) {
        if (o instanceof Calendar cal) {
            return DateFormat.getDateInstance().format(cal.getTime());
        } else {
            return o.toString();
        }
    }

    private static void display(String title, Object value) {
        if (value != null) {
            System.out.println(title + " " + format(value));
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + ExtractMetadata.class.getName() + " <input-pdf>");
    }
}
