package study.ywork.doc.itext.stream;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.io.RandomAccessFile;

public class EmbedFontPostFacto {
    private static final String[] RESULT = {
            "embedFontPostFacto_with_font.pdf",
            "embedFontPostFacto_without_font.pdf"
    };

    private static final String DEST = RESULT[0];
    private static final String FONT = "src/main/resources/fonts/wds011402.ttf";
    private static final String FONTNAME = "WaltDisneyScriptv4.1";

    public static void main(String[] args) {
        new EmbedFontPostFacto().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) {
        createPdf(RESULT[1]);
        changePdf(RESULT[1], dest);
    }

    private void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            font.setSubset(false);
            doc.add(new Paragraph("iText in Action").setFont(font).setFontSize(60));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void changePdf(String src, String dest) {
        byte[] fontBytes;
        try (RandomAccessFile raf = new RandomAccessFile(FONT, "r")) {
            fontBytes = new byte[(int) raf.length()];
            raf.readFully(fontBytes);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return;
        }

        PdfStream stream = new PdfStream(fontBytes);
        stream.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
        stream.put(new PdfName("Length1"), new PdfNumber(fontBytes.length));


        try (PdfReader reader = new PdfReader(src);
             PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(dest))) {
            PdfName tempFontName = new PdfName(FONTNAME);
            for (int i = 0; i < pdfDoc.getNumberOfPdfObjects(); i++) {
                PdfObject object = pdfDoc.getPdfObject(i);
                if (object == null || !object.isDictionary()) {
                    continue;
                }

                PdfDictionary font = (PdfDictionary) object;
                if (PdfName.FontDescriptor.equals(font.get(PdfName.Type))
                        && tempFontName.equals(font.get(PdfName.FontName))) {
                    font.put(PdfName.FontFile, stream);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
