package study.ywork.web.test.response.body;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class MyWebConfig {
    @Bean
    public MyController userController() {
        return new MyController();
    }
}
