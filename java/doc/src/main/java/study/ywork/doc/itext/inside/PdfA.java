package study.ywork.doc.itext.inside;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfa.PdfADocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfA {
    private static final String DEST = "pdfA.pdf";
    private static final String FONT = "src/main/resources/fonts/FreeSans.ttf";
    private static final String IMAGE_DIR = "src/main/resources/images/";

    public static void main(String[] args) {
        new PdfA().manipulatePdf(DEST);
    }

    private void manipulatePdf(String dest) {
        createPdfA(dest);
    }

    public void createPdfA(String dest) {
        try (InputStream is = new FileInputStream(IMAGE_DIR + "srgb.icm");
             PdfADocument pdfADocument = new PdfADocument(new PdfWriter(dest),
                     PdfAConformanceLevel.PDF_A_1B,
                     new PdfOutputIntent("Custom", "",
                             "https://www.color.org", "sRGB IEC61966-2.1", is));
             Document doc = new Document(pdfADocument)) {
            pdfADocument.addNewPage();
            PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.CP1252, EmbeddingStrategy.PREFER_EMBEDDED);
            doc.add(new Paragraph("Hello World").setFont(font));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
