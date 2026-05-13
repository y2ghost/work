package study.ywork.doc.itext.hello;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HelloZip {
    private static final String DEST = "helloZip.zip";

    public static void main(String[] args) throws IOException {
        new HelloZip().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        FileOutputStream fos = new FileOutputStream(DEST);
        try (ZipOutputStream zip = new ZipOutputStream(fos)) {
            for (int i = 1; i <= 3; i++) {
                ZipEntry entry = new ZipEntry("hello_" + i + ".pdf");
                zip.putNextEntry(entry);
                writePdf(zip, "Hello " + i);
                zip.closeEntry();
            }
        }
    }

    private void writePdf(ZipOutputStream out, String text) {
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        writer.setCloseStream(false);
        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph(text));
        doc.close();
    }
}
