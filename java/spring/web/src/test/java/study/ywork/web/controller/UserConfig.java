package study.ywork.web.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class UserConfig implements WebMvcConfigurer {
    @Bean
    public UserController getUserController() {
        return new UserController();
    }

    /*
     * 配置使用自定义参数media，默认的路径扩展内容协商策略功能仍在
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.TEXT_HTML);
        configurer.favorParameter(true);
        configurer.parameterName("media");
    }
}
