package study.ywork.doc.itext.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class LaunchAction {
    private static final String DEST = "launchAction.pdf";

    public static void main(String[] args) throws IOException {
        new LaunchAction().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfAction action = new PdfAction().put(PdfName.S, PdfName.Launch);
            PdfDictionary pdfDictionary = new PdfDictionary();
            pdfDictionary.put(PdfName.F, new PdfName("c:/windows/notepad.exe"));
            pdfDictionary.put(PdfName.P, new PdfName("test.txt"));
            pdfDictionary.put(PdfName.O, new PdfName("open"));
            pdfDictionary.put(PdfName.D, new PdfName("D:\\"));
            action.put(PdfName.Win, pdfDictionary);
            Paragraph p = new Paragraph(new Link("Click to open test.txt in Notepad.", action));
            doc.add(p);
        }
    }
}
