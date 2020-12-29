package study.ywork.web.test.request.mapping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfigHttpMethods {
    @Bean
    public UserControllerHttpMethods myMvcController() {
        return new UserControllerHttpMethods();
    }
}
