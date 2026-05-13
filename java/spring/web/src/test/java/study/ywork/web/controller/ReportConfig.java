package study.ywork.web.controller;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import study.ywork.web.converter.ReportConverter;

@EnableWebMvc
@Configuration
public class ReportConfig implements WebMvcConfigurer {
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ReportConverter());
    }

    @Bean
    ReportController getReportController() {
        return new ReportController();
    }
}
