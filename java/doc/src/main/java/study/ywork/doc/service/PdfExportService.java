package study.ywork.doc.service;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.util.Date;

@Service
public class PdfExportService {
    private final SpringTemplateEngine templateEngine;

    public PdfExportService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void exportPdf(HttpServletResponse response, String name, String userAgent) throws IOException {
        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("timestamp", new Date());
        ctx.setVariable("userAgent", userAgent);

        try {
            String processedHtml = templateEngine.process("pdf-template", ctx);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=report.pdf");
            ITextRenderer renderer = new ITextRenderer();
            configureChineseFont(renderer);
            ClassPathResource imgResource = new ClassPathResource("/images/");
            String baseUrl = imgResource.getURL().toString();
            renderer.getSharedContext().setBaseURL(baseUrl);
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(response.getOutputStream());
            renderer.finishPDF();
        } catch (DocumentException ex) {
            throw new IOException("导出PDF异常", ex);
        }
    }

    private void configureChineseFont(ITextRenderer renderer) throws DocumentException {
        try {
            ClassPathResource fontResource = new ClassPathResource("/fonts/FreeSans.ttf");
            String fontPath = fontResource.getURL().toString();
            renderer.getFontResolver().addFont(
                    fontPath,
                    BaseFont.IDENTITY_H,
                    BaseFont.NOT_EMBEDDED
            );
        } catch (Exception ex) {
            throw new DocumentException("字体加载失败: " + ex.getMessage());
        }
    }
}
