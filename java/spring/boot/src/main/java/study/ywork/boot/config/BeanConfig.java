package study.ywork.boot.config;

import java.util.Arrays;
import javax.servlet.ServletContextListener;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import study.ywork.boot.component.SimpleServletListener;
import study.ywork.boot.controller.EchoServlet;
import study.ywork.boot.converter.EmptyConverter;
import study.ywork.boot.filter.PrintUrlFilter;

/*
 * 配置Bean的配置类
 */
@Configuration
public class BeanConfig {
    // 添加自定义的转换类
    @Bean
    public HttpMessageConverters additionalConverters() {
        EmptyConverter converter = new EmptyConverter();
        return new HttpMessageConverters(converter);
    }

    // 覆盖默认的转换类列表，需要时添加@Bean注解
    public HttpMessageConverters defaultConverters() {
        EmptyConverter converter = new EmptyConverter();
        return new HttpMessageConverters(false, Arrays.asList(converter));
    }

    // 和additionalConverters方法作用相同，需要时添加@Bean注解
    public HttpMessageConverter<String> emptyConverter() {
        return new EmptyConverter();
    }

    // 可以通过注解的Bean的方式，添加第三方的servlet, filter, listener等
    @Bean
    FilterRegistrationBean<PrintUrlFilter> myFilterRegistration() {
        FilterRegistrationBean<PrintUrlFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new PrintUrlFilter("ExternalFilter"));
        frb.setUrlPatterns(Arrays.asList("/servlet/echo2"));
        return frb;
    }

    @Bean
    ServletRegistrationBean<EchoServlet> myServletRegistration() {
        ServletRegistrationBean<EchoServlet> srb = new ServletRegistrationBean<EchoServlet>();
        srb.setServlet(new EchoServlet());
        srb.setUrlMappings(Arrays.asList("/servlet/echo2"));
        return srb;
    }

    @Bean
    ServletListenerRegistrationBean<ServletContextListener> myServletListener() {
        ServletListenerRegistrationBean<ServletContextListener> srb = new ServletListenerRegistrationBean<>();
        srb.setListener(new SimpleServletListener("ExternalServletListener"));
        return srb;
    }
}
