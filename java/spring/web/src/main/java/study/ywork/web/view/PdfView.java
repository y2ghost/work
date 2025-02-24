package study.ywork.web.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.web.domain.Report;

/*
 * PDF文件视图例子，配合BeanNameViewResolver接口对象使用
 */
@Component("appPdfView")
public class PdfView extends AbstractView {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=my-report.pdf");
        Report report = (Report) model.get("report");

        // 初始化IText
        PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(pdfWriter);
        Document pdfDocument = new Document(pdf);

        // 设置标题
        Paragraph title = new Paragraph(report.getReportName());
        title.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
        title.setFontSize(18f);
        title.setItalic();
        pdfDocument.add(title);

        // 输出ID信息
        Paragraph date = new Paragraph(formatDate(LocalDateTime.now()));
        date.setFontSize(16f);
        pdfDocument.add(date);

        // 输出内容
        Paragraph content = new Paragraph(report.getContent());
        pdfDocument.add(content);
        pdfDocument.close();
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    }
}
