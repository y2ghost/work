package study.ywork.doc.itext.hello;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

/**
 * 列表示例
 */
public class ListItemPdf {
    private static final String DEST = "list_item.pdf";

    public static void main(String[] args) throws IOException {
        new ListItemPdf().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        try (Document document = new Document(pdf)) {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            document.add(new Paragraph("Study PDF is:").setFont(font));
            // 创建PDF列表对象
            List list = new List()
                    .setSymbolIndent(12)
                    .setListSymbol("\u2022")
                    .setFont(font);
            // 添加列表项
            list.add(new ListItem("Never gonna give you up"))
                    .add(new ListItem("Never gonna let you down"))
                    .add(new ListItem("Never gonna run around and desert you"))
                    .add(new ListItem("Never gonna make you cry"))
                    .add(new ListItem("Never gonna say goodbye"))
                    .add(new ListItem("Never gonna tell a lie and hurt you"));
            document.add(list);
        }
    }
}
