package study.ywork.doc.itext.pdftool;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class PageInformation {
    private static final String DEST = "pageInformation.txt";
    private static final String MOVIE_TEMPLATES = "movieTemplates.pdf";
    private static final String HERO1 = "hero1.pdf";

    public static void main(String[] args) throws IOException {
        new PageInformation().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(dest));
        inspect(writer, MOVIE_TEMPLATES);
        inspect(writer, HERO1);
        writer.close();
    }

    public static void inspect(PrintWriter writer, String filename)
            throws IOException {
        try (PdfReader reader = new PdfReader(filename);
             PdfDocument pdfDoc = new PdfDocument(reader)) {
            writer.println(filename);
            writer.print("Number of pages: ");
            writer.println(pdfDoc.getNumberOfPages());
            Rectangle mediabox = pdfDoc.getDefaultPageSize();
            writer.print("Size of page 1: [");
            writer.print(mediabox.getLeft());
            writer.print(',');
            writer.print(mediabox.getBottom());
            writer.print(',');
            writer.print(mediabox.getRight());
            writer.print(',');
            writer.print(mediabox.getTop());
            writer.println("]");
            writer.print("Rotation of page 1: ");
            writer.println(pdfDoc.getFirstPage().getRotation());
            writer.print("Page size with rotation of page 1: ");
            writer.println(pdfDoc.getFirstPage().getPageSizeWithRotation());
            writer.print("Is rebuilt? ");
            writer.println(reader.hasRebuiltXref());
            writer.print("Is encrypted? ");
            writer.println(reader.isEncrypted());
            writer.println();
            writer.println();
            writer.flush();
        }
    }
}
