package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PageMode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePortableCollection {
    private CreatePortableCollection() {
    }

    public void doIt(String file) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Example of a portable collection");
                contentStream.endText();
            }

            PDEmbeddedFilesNameTreeNode efTree = new PDEmbeddedFilesNameTreeNode();
            PDComplexFileSpecification fs1 = new PDComplexFileSpecification();
            fs1.setFile("Test1.txt");
            fs1.setFileUnicode("Test1.txt");
            byte[] data1 = "This is the contents of the first embedded file".getBytes(StandardCharsets.ISO_8859_1);
            PDEmbeddedFile ef1 = new PDEmbeddedFile(doc, new ByteArrayInputStream(data1), COSName.FLATE_DECODE);
            ef1.setSubtype("text/plain");
            ef1.setSize(data1.length);
            ef1.setCreationDate(new GregorianCalendar());

            fs1.setEmbeddedFile(ef1);
            fs1.setEmbeddedFileUnicode(ef1);
            fs1.setFileDescription("The first file");

            PDComplexFileSpecification fs2 = new PDComplexFileSpecification();
            fs2.setFile("Test2.txt");
            fs2.setFileUnicode("Test2.txt");
            byte[] data2 = "This is the contents of the second embedded file".getBytes(StandardCharsets.ISO_8859_1);
            PDEmbeddedFile ef2 = new PDEmbeddedFile(doc, new ByteArrayInputStream(data2), COSName.FLATE_DECODE);
            ef2.setSubtype("text/plain");
            ef2.setSize(data2.length);
            ef2.setCreationDate(new GregorianCalendar());

            fs2.setEmbeddedFile(ef2);
            fs2.setEmbeddedFileUnicode(ef2);
            fs2.setFileDescription("The second file");

            Map<String, PDComplexFileSpecification> map = new HashMap<>();
            map.put("Attachment 1", fs1);
            map.put("Attachment 2", fs2);

            PDEmbeddedFilesNameTreeNode treeNode = new PDEmbeddedFilesNameTreeNode();
            treeNode.setNames(map);
            List<PDEmbeddedFilesNameTreeNode> kids = new ArrayList<>();
            kids.add(treeNode);
            efTree.setKids(kids);

            PDDocumentNameDictionary names = new PDDocumentNameDictionary(doc.getDocumentCatalog());
            names.setEmbeddedFiles(efTree);
            doc.getDocumentCatalog().setNames(names);

            doc.getDocumentCatalog().setPageMode(PageMode.USE_ATTACHMENTS);
            COSDictionary collectionDic = new COSDictionary();
            COSDictionary schemaDict = new COSDictionary();
            schemaDict.setItem(COSName.TYPE, COSName.COLLECTION_SCHEMA);
            COSDictionary sortDic = new COSDictionary();
            sortDic.setItem(COSName.TYPE, COSName.COLLECTION_SORT);
            sortDic.setString(COSName.A, "true");
            sortDic.setItem(COSName.S, COSName.getPDFName("fieldtwo"));
            collectionDic.setItem(COSName.TYPE, COSName.COLLECTION);
            collectionDic.setItem(COSName.SCHEMA, schemaDict);
            collectionDic.setItem(COSName.SORT, sortDic);
            collectionDic.setItem(COSName.VIEW, COSName.D);
            COSDictionary fieldDict1 = new COSDictionary();
            fieldDict1.setItem(COSName.TYPE, COSName.COLLECTION_FIELD);
            fieldDict1.setItem(COSName.SUBTYPE, COSName.S);
            fieldDict1.setString(COSName.N, "field header one (description)");
            fieldDict1.setInt(COSName.O, 1);
            COSDictionary fieldDict2 = new COSDictionary();
            fieldDict2.setItem(COSName.TYPE, COSName.COLLECTION_FIELD);
            fieldDict2.setItem(COSName.SUBTYPE, COSName.S);
            fieldDict2.setString(COSName.N, "field header two (name)");
            fieldDict2.setInt(COSName.O, 2);
            COSDictionary fieldDict3 = new COSDictionary();
            fieldDict3.setItem(COSName.TYPE, COSName.COLLECTION_FIELD);
            fieldDict3.setItem(COSName.SUBTYPE, COSName.N);
            fieldDict3.setString(COSName.N, "field header three (size)");
            fieldDict3.setInt(COSName.O, 3);
            schemaDict.setItem("fieldone", fieldDict1);
            schemaDict.setItem("fieldtwo", fieldDict2);
            schemaDict.setItem("fieldthree", fieldDict3);
            doc.getDocumentCatalog().getCOSObject().setItem(COSName.COLLECTION, collectionDic);
            doc.getDocumentCatalog().setVersion("1.7");

            COSDictionary ciDict1 = new COSDictionary();
            ciDict1.setItem(COSName.TYPE, COSName.COLLECTION_ITEM);
            ciDict1.setString("fieldone", fs1.getFileDescription());
            ciDict1.setString("fieldtwo", fs1.getFile());
            ciDict1.setInt("fieldthree", fs1.getEmbeddedFile().getSize());
            fs1.getCOSObject().setItem(COSName.CI, ciDict1);

            COSDictionary ciDict2 = new COSDictionary();
            ciDict2.setItem(COSName.TYPE, COSName.COLLECTION_ITEM);
            ciDict2.setString("fieldone", fs2.getFileDescription());
            ciDict2.setString("fieldtwo", fs2.getFile());
            ciDict2.setInt("fieldthree", fs2.getEmbeddedFile().getSize());
            fs2.getCOSObject().setItem(COSName.CI, ciDict2);
            doc.save(file);
        }
    }

    public static void main(String[] args) throws IOException {
        CreatePortableCollection app = new CreatePortableCollection();
        if (args.length != 1) {
            app.usage();
        } else {
            app.doIt(args[0]);
        }
    }

    private void usage() {
        System.err.println("usage: " + this.getClass().getName() + " <output-file>");
    }
}
