package study.ywork.web.test.request.format;

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
    public RegisterController getRegisterController() {
        return new RegisterController(getUserService());
    }

    @Bean
    public UserService getUserService() {
        return new UserService();
    }
}
