package study.ywork.web.test.myweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyWebConfig {
    @Bean
    public MyController myMvcController() {
        return new MyController();
    }
}
