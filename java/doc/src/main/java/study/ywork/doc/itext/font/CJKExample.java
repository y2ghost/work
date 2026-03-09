package study.ywork.doc.itext.font;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class CJKExample {
    private static final String DEST = "CJKExample.pdf";
    private static final String[][] MOVIES = {
        {
            "STSong-Light", "UniGB-UCS2-H",
            "Movie title: House of The Flying Daggers (China)",
            "directed by Zhang Yimou",
            "十面埋伏"
        },
        {
            "KozMinPro-Regular", "UniJIS-UCS2-H",
            "Movie title: Nobody Knows (Japan)",
            "directed by Hirokazu Koreeda",
            "誰も知らない"
        },
        {
            "HYGoThic-Medium", "UniKS-UCS2-H",
            "Movie title: '3-Iron' aka 'Bin-jip' (South-Korea)",
            "directed by Kim Ki-Duk",
            "빈집"
        }
    };

    public static void main(String[] args) {
        new CJKExample().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            PdfFont font;
            for (int i = 0; i < 3; i++) {
                font = PdfFontFactory.createFont(MOVIES[i][0], MOVIES[i][1], EmbeddingStrategy.PREFER_NOT_EMBEDDED);
                doc.add(new Paragraph(font.getFontProgram().getFontNames().getFontName()).setFont(font));
                for (int j = 2; j < 5; j++)
                    doc.add(new Paragraph(MOVIES[i][j]).setFont(font));
                doc.add(new Paragraph("\n"));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
