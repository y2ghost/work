package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdfwriter.compress.CompressParameters;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class CreatePDFA {
    private CreatePDFA() {
    }

    public static void main(String[] args) throws IOException, TransformerException {
        if (args.length != 3) {
            System.err.println("usage: " + CreatePDFA.class.getName() +
                    " <output-file> <Message> <ttf-file>");
            System.exit(1);
        }

        String file = args[0];
        String message = args[1];
        String fontfile = args[2];

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDFont font = PDType0Font.load(doc, new File(fontfile));

            if (!font.isEmbedded()) {
                throw new IllegalStateException("PDF/A compliance requires that all fonts used for"
                        + " text rendering in rendering modes other than rendering mode 3 are embedded.");
            }

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.beginText();
                contents.setFont(font, 12);
                contents.newLineAtOffset(100, 700);
                contents.showText(message);
                contents.endText();
            }

            XMPMetadata xmp = XMPMetadata.createXMPMetadata();
            try {
                DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
                dc.setTitle(file);

                PDFAIdentificationSchema id = xmp.createAndAddPDFAIdentificationSchema();
                id.setPart(1);
                id.setConformance("B");

                XmpSerializer serializer = new XmpSerializer();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                serializer.serialize(xmp, baos, true);

                PDMetadata metadata = new PDMetadata(doc);
                metadata.importXMPMetadata(baos.toByteArray());
                doc.getDocumentCatalog().setMetadata(metadata);
            } catch (BadFieldValueException e) {
                throw new IllegalArgumentException(e);
            }

            InputStream colorProfile = CreatePDFA.class.getResourceAsStream("/pdfbox/pdfa/sRGB.icc");
            PDOutputIntent intent = new PDOutputIntent(doc, colorProfile);
            String info = "sRGB IEC61966-2.1";
            intent.setInfo(info);
            intent.setOutputCondition(info);
            intent.setOutputConditionIdentifier(info);
            intent.setRegistryName("https://www.color.org");
            doc.getDocumentCatalog().addOutputIntent(intent);
            doc.save(file, CompressParameters.NO_COMPRESSION);
        }
    }
}
