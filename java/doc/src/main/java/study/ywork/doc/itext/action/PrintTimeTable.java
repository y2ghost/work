package study.ywork.doc.itext.action;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import study.ywork.doc.util.FileUtils;

import java.io.IOException;

public class PrintTimeTable {
    private static final String DEST = "printTimeTable.pdf";
    private static final String RESOURCE = "src/main/resources/js/print_page.js";
    private static final String MOVIE_TEMPLATES = "src/main/resources/pdfs/movieTemplates.pdf";

    public static void main(String[] args) throws IOException {
        PrintTimeTable application = new PrintTimeTable();
        application.manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        try (PdfReader reader = new PdfReader(MOVIE_TEMPLATES);
             PdfWriter writer = new PdfWriter(DEST);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {
            pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(FileUtils.readFileToString(RESOURCE).replace("\r\n", "\n")));
            PdfAction action = PdfAction.createJavaScript("app.alert('Think before you print!');");
            action.next(PdfAction.createJavaScript("printCurrentPage(this.pageNum);"));
            action.next(PdfAction.createURI("https://www.panda.org/savepaper/"));
            Link link = new Link("print this page", action);
            Paragraph paragraph = new Paragraph(link);

            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfCanvas canvas = new PdfCanvas(pdfDoc.getPage(i));
                try (Canvas pageCanvas = new Canvas(canvas, pdfDoc.getPage(i).getPageSize())) {
                    pageCanvas.showTextAligned(paragraph, 816, 18, i,
                            TextAlignment.RIGHT, VerticalAlignment.MIDDLE, 0);
                }
            }
        }
    }


}
