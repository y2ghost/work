package study.ywork.web.test.request.bind;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import study.ywork.web.test.config.ViewConfig;

@EnableWebMvc
@Configuration
@Import(ViewConfig.class)
public class YyTradeConfig implements WebMvcConfigurer {

    @Bean
    public YyTradeController getYyTradeController() {
        return new YyTradeController();
    }

    @Bean
    public YyTradeService getYytradeService() {
        return new YyTradeService();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new YyTradeConverter(getYytradeService()));
    }
}
