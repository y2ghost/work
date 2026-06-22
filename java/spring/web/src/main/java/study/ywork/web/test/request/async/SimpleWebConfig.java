package study.ywork.web.test.request.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class SimpleWebConfig {
    @Bean
    public SimpleController myController() {
        return new SimpleController();
    }
}