package study.ywork.web.test.request.mapping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfigVariant {
    @Bean
    public VariantController myMvcController() {
        return new VariantController();
    }
}
