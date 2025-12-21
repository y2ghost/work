package study.ywork.doc.itext.stream;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

public class ListUsedFonts {
    private static final String DEST = "listUsedFonts.txt";
    private static final String FONT_TYPES = "src/main/resources/pdfs/FontTypes.pdf";

    public static void main(String[] args) {
        new ListUsedFonts().manipulatePdf();
    }

    public void manipulatePdf() {
        try {
            Set<String> set = listFonts(FONT_TYPES);
            StringBuilder buffer = new StringBuilder();

            for (String fontName : set) {
                buffer.append(fontName).append("\n");
            }

            try (PrintWriter out = new PrintWriter(new FileOutputStream(DEST))) {
                out.print(buffer);
                out.flush();
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private Set<String> listFonts(String src) throws IOException {
        Set<String> set = new TreeSet<>();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            for (int k = 1; k <= pdfDoc.getNumberOfPages(); ++k) {
                PdfDictionary resources = pdfDoc.getPage(k).getPdfObject().getAsDictionary(PdfName.Resources);
                processResource(set, resources);
            }
        }
        return set;
    }

    public static void processResource(Set<String> set, PdfDictionary resource) {
        if (resource == null) {
            return;
        }

        PdfDictionary objects = resource.getAsDictionary(PdfName.XObject);
        if (objects != null) {
            for (PdfName key : objects.keySet()) {
                processResource(set, objects.getAsDictionary(key));
            }
        }

        PdfDictionary fonts = resource.getAsDictionary(PdfName.Font);
        if (fonts == null) {
            return;
        }

        for (PdfName key : fonts.keySet()) {
            PdfDictionary font = fonts.getAsDictionary(key);
            String name = font.getAsName(PdfName.BaseFont).toString();
            if (name.length() > 8 && name.charAt(7) == '+') {
                name = String.format("%s subset (%s)", name.substring(8), name.substring(1, 7));
            } else {
                name = name.substring(1);
                PdfDictionary desc = font.getAsDictionary(PdfName.FontDescriptor);
                if (desc == null)
                    name += " nofontdescriptor";
                else if (desc.get(PdfName.FontFile) != null)
                    name += " (Type 1) embedded";
                else if (desc.get(PdfName.FontFile2) != null)
                    name += " (TrueType) embedded";
                else if (desc.get(PdfName.FontFile3) != null)
                    name += " (" + font.getAsName(PdfName.Subtype).toString().substring(1) + ") embedded";
            }
            set.add(name);
        }
    }
}
