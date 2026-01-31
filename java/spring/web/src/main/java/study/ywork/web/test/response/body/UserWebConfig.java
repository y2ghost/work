package study.ywork.web.test.response.body;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import study.ywork.web.test.config.ViewConfig;

@EnableWebMvc
@Configuration
@Import(ViewConfig.class)
public class UserWebConfig {
    @Bean
    public UserController getUserController() {
        return new UserController(getUserService());
    }

    @Bean
    public UserRestController getUserRestController() {
        return new UserRestController(getUserService());
    }

    @Bean
    public UserService getUserService() {
        return new UserService();
    }
}
