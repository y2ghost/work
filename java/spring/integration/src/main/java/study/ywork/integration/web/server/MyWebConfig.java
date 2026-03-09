package study.ywork.integration.web.server;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

@EnableWebMvc
@EnableWebSocket
@Configuration
@Import(MyBeanConfig.class)
@ComponentScan
public class MyWebConfig implements WebMvcConfigurer, WebSocketConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源可以设置缓存时间，浏览器会看到Cache-Control头
        registry.addResourceHandler("/**").addResourceLocations("/static/").setCachePeriod(30);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new EchoSocketHandler(), "/echo-handler").setAllowedOrigins("*");
    }
}
