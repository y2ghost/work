package study.ywork.web.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import study.ywork.web.controller.BeanNameTwoController;

import java.util.Locale;

/*
 * 定义的Bean名称为DispatcherServlet.XXX的时候表示覆盖默认的同类型对象
 * 所以定义Bean名称的时候还是需要谨慎，不然很容易和Spring MVC框架内置的名称相同
 * 或者与第三方库的定义名称相同，导致覆盖风险，不太清楚谁覆盖谁的定义
 */
@Configuration
@EnableWebMvc
public class BeanConfig {
    private static final String UTF8 = "UTF-8";

    /*
     * 自定义国际化语言配置，该Bean名称就是AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME
     * 会覆盖它的实例，看自己的需求而定
     */
    @Bean("messageSource")
    public MessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // 为了方便自定义的前缀其实和Hibernate Validator验证器需要的国际化前缀配置一致
        messageSource.setBasenames("ValidationMessages");
        messageSource.setDefaultEncoding(UTF8);
        return messageSource;
    }

    // 使用SessionLocaleResolver对象解析国际化，避免默认的对象不支持更改国际化
    @Bean(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
    public LocaleContextResolver getLocaleResolver() {
        SessionLocaleResolver r = new SessionLocaleResolver();
        r.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return r;
    }

    /*
     * 注意名称和WebMvcConfigurationSupport::handlerExceptionResolver函数(注解了@Bean)
     * 相同，所以存在覆盖现象，可能为了覆盖DispatcherServlet的默认HandlerExceptionResolver接口对象 目的如此吧
     * 暂时不用: @Bean(DispatcherServlet.HANDLER_EXCEPTION_RESOLVER_BEAN_NAME)
     * 此处需要默认的HandlerExceptionResolver实现，配合下面的一起完成演示例子
     */
    @Bean("showExceptionResolver")
    HandlerExceptionResolver getHandlerExceptionResolver() {
        return new ShowExceptionResolver();
    }

    // Bean的名称作为URL地址，演示通过注解@Bean指定URL
    @Bean("/bean-name-two")
    BeanNameTwoController getBeanNameTwoController() {
        return new BeanNameTwoController();
    }

    @Bean
    public GroovyMarkupConfigurer groovyMarkupConfigurer() {
        // 这个Bean对象是配合Groovy视图解析对象使用
        GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
        configurer.setResourceLoaderPath("/WEB-INF/views/");
        return configurer;
    }

    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        // 配合FreeMarker视图解析对象使用
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("/WEB-INF/views/");
        configurer.setDefaultEncoding(UTF8);
        return configurer;
    }

    @Bean("appJsonView")
    public View getJsonView() {
        return new MappingJackson2JsonView();
    }
}
