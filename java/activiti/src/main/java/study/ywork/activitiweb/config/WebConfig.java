package study.ywork.activitiweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import study.ywork.activitiweb.security.LoginFailureHandler;
import study.ywork.activitiweb.security.LoginSuccessHandler;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private LoginSuccessHandler loginSuccessHandler;
    private LoginFailureHandler loginFailureHandler;
    private static final String LOGIN_API = "/login";

    public WebConfig(LoginSuccessHandler loginSuccessHandler,
        LoginFailureHandler loginFailureHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("/bpmn/**").addResourceLocations("classpath:/resources/bpmn/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin().loginPage(LOGIN_API).loginProcessingUrl(LOGIN_API).successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler).and().authorizeRequests()
            .antMatchers(LOGIN_API, "/demo-login.html", "/demo-login1.html", "/layuimini/page/login-1.html").permitAll()
            .anyRequest().permitAll().and().logout().permitAll().and().csrf().disable().headers().frameOptions()
            .disable();
        return http.build();
    }
}
