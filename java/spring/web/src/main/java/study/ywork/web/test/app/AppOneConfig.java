package study.ywork.web.test.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import study.ywork.web.domain.GlobalStyle;

@EnableWebMvc
@Configuration
public class AppOneConfig {
    @Bean
    public AppOneController myController() {
        return new AppOneController();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION)
    public GlobalStyle getGlobalStyle() {
        return new GlobalStyle();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }
}
