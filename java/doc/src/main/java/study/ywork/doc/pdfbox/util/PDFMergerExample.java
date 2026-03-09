package study.ywork.doc.pdfbox.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdfwriter.compress.CompressParameters;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

public class PDFMergerExample {
    private static final Log LOG = LogFactory.getLog(PDFMergerExample.class);

    public InputStream merge(final List<RandomAccessRead> sources) throws IOException {
        String title = "My title";
        String creator = "Alexander Kriegisch";
        String subject = "Subject with umlauts ÄÖÜ";

        try (COSStream cosStream = new COSStream();
             ByteArrayOutputStream mergedPDFOutputStream = new ByteArrayOutputStream()) {
            PDFMergerUtility pdfMerger = createPDFMergerUtility(sources, mergedPDFOutputStream);
            // PDF和XMP属性必须相同，否则文档不兼容PDF/A
            PDDocumentInformation pdfDocumentInfo = createPDFDocumentInfo(title, creator, subject);
            PDMetadata xmpMetadata = createXMPMetadata(cosStream, title, creator, subject);
            pdfMerger.setDestinationDocumentInformation(pdfDocumentInfo);
            pdfMerger.setDestinationMetadata(xmpMetadata);

            LOG.info("Merging " + sources.size() + " source documents into one PDF");
            pdfMerger.mergeDocuments(IOUtils.createMemoryOnlyStreamCache(), CompressParameters.NO_COMPRESSION);
            LOG.info("PDF merge successful, size = {" + mergedPDFOutputStream.size() + "} bytes");
            return new ByteArrayInputStream(mergedPDFOutputStream.toByteArray());
        } catch (BadFieldValueException | TransformerException e) {
            throw new IOException("PDF merge problem", e);
        } finally {
            sources.forEach(IOUtils::closeQuietly);
        }
    }

    private PDFMergerUtility createPDFMergerUtility(List<RandomAccessRead> sources,
                                                    ByteArrayOutputStream mergedPDFOutputStream) {
        LOG.info("Initialising PDF merge utility");
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.addSources(sources);
        pdfMerger.setDestinationStream(mergedPDFOutputStream);
        return pdfMerger;
    }

    private PDDocumentInformation createPDFDocumentInfo(String title, String creator, String subject) {
        LOG.info("Setting document info (title, author, subject) for merged PDF");
        PDDocumentInformation documentInformation = new PDDocumentInformation();
        documentInformation.setTitle(title);
        documentInformation.setCreator(creator);
        documentInformation.setSubject(subject);
        return documentInformation;
    }

    private PDMetadata createXMPMetadata(COSStream cosStream, String title, String creator, String subject)
            throws BadFieldValueException, TransformerException, IOException {
        LOG.info("Setting XMP metadata (title, author, subject) for merged PDF");
        XMPMetadata xmpMetadata = XMPMetadata.createXMPMetadata();

        // PDF/A-1b 属性
        PDFAIdentificationSchema pdfaSchema = xmpMetadata.createAndAddPDFAIdentificationSchema();
        pdfaSchema.setPart(1);
        pdfaSchema.setConformance("B");

        DublinCoreSchema dublinCoreSchema = xmpMetadata.createAndAddDublinCoreSchema();
        dublinCoreSchema.setTitle(title);
        dublinCoreSchema.addCreator(creator);
        dublinCoreSchema.setDescription(subject);

        XMPBasicSchema basicSchema = xmpMetadata.createAndAddXMPBasicSchema();
        Calendar creationDate = Calendar.getInstance();
        basicSchema.setCreateDate(creationDate);
        basicSchema.setModifyDate(creationDate);
        basicSchema.setMetadataDate(creationDate);
        basicSchema.setCreatorTool(creator);

        try (OutputStream cosXMPStream = cosStream.createOutputStream()) {
            new XmpSerializer().serialize(xmpMetadata, cosXMPStream, true);
            cosStream.setName(COSName.TYPE, "Metadata");
            cosStream.setName(COSName.SUBTYPE, "XML");
            return new PDMetadata(cosStream);
        }
    }
}
