package study.ywork.web.config;

import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import study.ywork.web.converter.CSVConverter;
import study.ywork.web.converter.ReportConverter;
import study.ywork.web.converter.YamlConverter;
import study.ywork.web.interceptor.CommonInterceptor;

@EnableWebMvc
@Configuration
@Import(BeanConfig.class)
@ComponentScan(basePackages = "study.ywork.web", excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "study.ywork.web.test.*"))
public class MainWebConfig implements WebMvcConfigurer {
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // 注意注册的ViewResolver接口对象有顺序的之分，多个对象存在的话
        // 只要其中一个对象匹配或是处理完毕后，就会结束
        // 但有些对象不支持链式，也就是到它处理的时候，都会返回一个视图结果
        // 注册BeanNameViewResolver对象，支持链式
        registry.beanName();
        // 注册GroovyMarkupViewResolver对象，支持链式
        registry.groovy();
        // 注册GroovyMarkupViewResolver对象，支持链式
        registry.freeMarker();
        // JSP不支持链式
        registry.jsp("/WEB-INF/views/", ".jsp");
    }

    /*
     * 演示ViewControllerRegistry类的用法
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/sayhi").setViewName("sayhi");
        registry.addStatusController("/status-test", HttpStatus.SERVICE_UNAVAILABLE);
        registry.addRedirectViewController("/sayhi2", "/sayhi").setStatusCode(HttpStatus.MOVED_PERMANENTLY);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
        registry.addInterceptor(new CommonInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源可以设置缓存时间，浏览器会看到Cache-Control头
        registry.addResourceHandler("/static/**").addResourceLocations("/static/").setCachePeriod(30);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ReportConverter());
        converters.add(new CSVConverter<>());
        converters.add(new YamlConverter<>());
    }

    // 自定义异步请求的TaskExecutor接口对象
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(10);
        t.setMaxPoolSize(100);
        t.setQueueCapacity(50);
        t.setAllowCoreThreadTimeOut(true);
        t.setKeepAliveSeconds(120);
        t.initialize();
        configurer.setTaskExecutor(t);
    }

    /*
     * 配置使用自定义参数，默认的路径扩展内容协商策略功能仍在
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.TEXT_HTML);
        configurer.favorParameter(true);
        configurer.parameterName("format");
    }
}
