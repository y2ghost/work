package study.ywork.boot.config;

import java.util.Arrays;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import study.ywork.boot.converter.EmptyConverter;

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
}
