package study.ywork.doc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import study.ywork.doc.view.ExcelView;
import study.ywork.doc.view.PdfView;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {
    @Bean("excelView")
    public ExcelView excelView() {
        return new ExcelView();
    }

    @Bean("pdfView")
    public PdfView pdfView() {
        return new PdfView();
    }
}
