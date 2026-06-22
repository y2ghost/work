package study.ywork.doc.itext.page;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.utils.TaggedPdfReaderTool;

import java.io.FileOutputStream;
import java.io.IOException;

public class ParseTaggedPdf {
    private static final String DEST = "parseTaggedPdf.xml";
    private static final String STRUCTURED_CONTENT = "structuredContent.pdf";

    public static void main(String[] args) {
        new ParseTaggedPdf().manipulatePdf();
    }

    public void manipulatePdf() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(STRUCTURED_CONTENT))) {
            TaggedPdfReaderTool readerTool = new TaggedPdfReaderTool(pdfDoc);
            readerTool.convertToXml(new FileOutputStream(DEST));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
