package study.ywork.web.component;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/*
 * 目的了解注册的HttpMessageConverter接口对象
 */
@Component
public class PrintMessageConverters {
    private final RequestMappingHandlerAdapter adapter;

    public PrintMessageConverters(RequestMappingHandlerAdapter adapter) {
        this.adapter = adapter;
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        System.out.println("-- 打印注册的HttpMessageConverter接口对象 --");
        System.out.println("Context: " + event.getApplicationContext());
        adapter.getMessageConverters().forEach(System.out::println);
        System.out.println("-- 打印结束 --");
    }
}
