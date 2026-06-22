package study.ywork.doc.itext.protect;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.xmp.PdfConst;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileOutputStream;
import java.io.IOException;

public class MetadataXmp {
    private static final String METADATA_PDF
        = "metadataPdf.pdf";
    private static final String[] RESULT = {
        "metadataXmp_xmp_metadata.pdf",
        "metadataXmp_xmp_metadata_automatic.pdf",
        "metadataXmp_xmp_metadata_added.pdf"
    };
    private static final String RESULT_XML = "metadataXmp_xmp.xml";

    public static void main(String[] args) {
        new MetadataXmp().manipulatePdf();
    }

    private Paragraph newHellParagraph() {
        return new Paragraph("Hello World");
    }

    public void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            XMPMeta xmp = XMPMetaFactory.create();
            xmp.appendArrayItem(XMPConst.NS_DC, PdfConst.Subject, new PropertyOptions(PropertyOptions.ARRAY), "Hello World", null);
            xmp.appendArrayItem(XMPConst.NS_DC, PdfConst.Subject, "XMP & Metadata");
            xmp.appendArrayItem(XMPConst.NS_DC, PdfConst.Subject, "Metadata");
            xmp.appendArrayItem(XMPConst.NS_DC, PdfConst.Keywords, new PropertyOptions(PropertyOptions.ARRAY), "Hello World, XMP, Metadata", null);
            xmp.appendArrayItem(XMPConst.NS_DC, PdfConst.Version, new PropertyOptions(PropertyOptions.ARRAY), "1.4", null);
            pdfDoc.setXmpMetadata(xmp);
            doc.add(newHellParagraph());
        } catch (IOException | XMPException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void createPdfAutomatic(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfDocumentInfo info = pdfDoc.getDocumentInfo();
            info.setTitle("Hello World example")
                .setSubject("This example shows how to add metadata & XMP")
                .setKeywords("Metadata, iText, step 3")
                .setCreator("My program using 'iText'")
                .setAuthor("Bruno Lowagie & Paulo Soares");

            doc.add(newHellParagraph());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void manipulatePdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src),
            new PdfWriter(dest, new WriterProperties().addXmpMetadata()))) {
            // NOOP
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void readXmpMetadata(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src));
             FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] b = pdfDoc.getXmpMetadata();
            fos.write(b, 0, b.length);
            fos.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void manipulatePdf() {
        createPdf(RESULT[0]);
        createPdfAutomatic(RESULT[1]);
        manipulatePdf(METADATA_PDF, RESULT[2]);
        readXmpMetadata(RESULT[2], RESULT_XML);
    }
}
