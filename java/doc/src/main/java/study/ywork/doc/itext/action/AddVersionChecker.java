package study.ywork.doc.itext.action;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AddVersionChecker {
    private static final String DEST = "addVersionChecker.pdf";
    private static final String RESOURCE = "src/main/resources/js/viewer_version.js";
    private static final String HELLO_WORLD = "src/main/resources/pdfs/helloWorld.pdf";

    public static void main(String[] args) throws IOException {
        AddVersionChecker checker = new AddVersionChecker();
        checker.manipulatePdf();
    }

    protected static String readFileToString(String path) throws IOException {
        File file = new File(path);
        byte[] jsBytes = new byte[(int) file.length()];

        try (FileInputStream f = new FileInputStream(file)) {
            f.read(jsBytes);
        }
        
        return new String(jsBytes).replace("\r\n", "\n");
    }

    public void manipulatePdf() throws IOException {
        try (PdfReader reader = new PdfReader(HELLO_WORLD);
             PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(DEST))) {
            pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(readFileToString(RESOURCE)));
        }
    }
}
