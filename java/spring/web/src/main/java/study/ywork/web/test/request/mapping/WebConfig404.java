package study.ywork.web.test.request.mapping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig404 {
    @Bean
    public UserController404 myMvcController() {
        return new UserController404();
    }
}
