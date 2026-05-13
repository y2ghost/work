package study.ywork.boot.component;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/*
 * 目的可以查看自定义的HttpMessageConverter
 */
@Component
public class ShowConverters {
    private RequestMappingHandlerAdapter handlerAdapter;

    public ShowConverters(RequestMappingHandlerAdapter handlerAdapter) {
        this.handlerAdapter = handlerAdapter;
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        System.out.println("查看注册的MessageConverters");
        handlerAdapter.getMessageConverters().stream().forEach(System.out::println);
    }
}
