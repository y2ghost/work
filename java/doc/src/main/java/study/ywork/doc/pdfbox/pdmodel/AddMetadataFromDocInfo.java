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
import org.apache.xmpbox.xml.XmpSerializer;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

public final class AddMetadataFromDocInfo {
    private AddMetadataFromDocInfo() {
    }

    public static void main(String[] args) throws IOException, TransformerException {
        if (args.length != 2) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                if (document.isEncrypted()) {
                    System.err.println("Error: Cannot add metadata to encrypted document.");
                    System.exit(1);
                }

                PDDocumentCatalog catalog = document.getDocumentCatalog();
                PDDocumentInformation info = document.getDocumentInformation();
                XMPMetadata metadata = XMPMetadata.createXMPMetadata();
                AdobePDFSchema pdfSchema = metadata.createAndAddAdobePDFSchema();
                pdfSchema.setKeywords(info.getKeywords());
                pdfSchema.setProducer(info.getProducer());
                XMPBasicSchema basicSchema = metadata.createAndAddXMPBasicSchema();
                basicSchema.setModifyDate(info.getModificationDate());
                basicSchema.setCreateDate(info.getCreationDate());
                basicSchema.setCreatorTool(info.getCreator());
                basicSchema.setMetadataDate(new GregorianCalendar());
                DublinCoreSchema dcSchema = metadata.createAndAddDublinCoreSchema();
                dcSchema.setTitle(info.getTitle());
                dcSchema.addCreator("PDFBox");
                dcSchema.setDescription(info.getSubject());
                PDMetadata metadataStream = new PDMetadata(document);
                catalog.setMetadata(metadataStream);
                XmpSerializer serializer = new XmpSerializer();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                serializer.serialize(metadata, baos, false);
                metadataStream.importXMPMetadata(baos.toByteArray());
                document.save(args[1]);
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + AddMetadataFromDocInfo.class.getName() + " <input-pdf> <output-pdf>");
    }
}
