package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PageMode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class EmbeddedFiles {
    private EmbeddedFiles() {
    }

    public void doIt(String file) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Go to Document->File Attachments to View Embedded Files");
                contentStream.endText();
            }

            // 存储内嵌文件信息
            PDEmbeddedFilesNameTreeNode efTree = new PDEmbeddedFilesNameTreeNode();
            PDComplexFileSpecification fs = new PDComplexFileSpecification();
            fs.setFile("Test.txt");
            fs.setFileUnicode("Test.txt");
            byte[] data = "This is the contents of the embedded file".getBytes(StandardCharsets.ISO_8859_1);
            ByteArrayInputStream fakeFile = new ByteArrayInputStream(data);
            PDEmbeddedFile ef = new PDEmbeddedFile(doc, fakeFile);
            ef.setSubtype("text/plain");
            ef.setSize(data.length);
            ef.setCreationDate(new GregorianCalendar());
            fs.setEmbeddedFile(ef);
            fs.setEmbeddedFileUnicode(ef);
            fs.setFileDescription("Very interesting file");

            PDEmbeddedFilesNameTreeNode treeNode = new PDEmbeddedFilesNameTreeNode();
            treeNode.setNames(Collections.singletonMap("My first attachment", fs));
            List<PDEmbeddedFilesNameTreeNode> kids = new ArrayList<>();
            kids.add(treeNode);
            efTree.setKids(kids);
            PDDocumentNameDictionary names = new PDDocumentNameDictionary(doc.getDocumentCatalog());
            names.setEmbeddedFiles(efTree);
            doc.getDocumentCatalog().setNames(names);
            doc.getDocumentCatalog().setPageMode(PageMode.USE_ATTACHMENTS);
            doc.save(file);
        }
    }

    public static void main(String[] args) throws IOException {
        EmbeddedFiles app = new EmbeddedFiles();
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
