package study.ywork.web.controller;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Map;

/*
 * 打印HandlerMapping和HandlerAdapter接口对象注册信息
 */
@RestController
@RequestMapping("/handler")
public class ShowHandlerController {
    private final ApplicationContext context;

    public ShowHandlerController(ApplicationContext context) {
        this.context = context;
    }

    @GetMapping(value = "/mapping", produces = "text/html;charset=UTF-8")
    public String showMapping() {
        Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
                HandlerMapping.class, true, false);
        StringBuilder builder = new StringBuilder("HandlerMapping接口对象列表: <br/><br/>");
        matchingBeans
                .forEach((k, v) -> builder.append(String.format("<li>%s=%s</li>", k, v.getClass().getSimpleName())));
        return builder.toString();
    }

    @GetMapping(value = "/adapter", produces = "text/html;charset=UTF-8")
    public String showAdapters() {
        Map<String, HandlerAdapter> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
                HandlerAdapter.class, true, false);
        StringBuilder builder = new StringBuilder("HandlerAdapter接口对象列表: <br/><br/>");
        matchingBeans
                .forEach((k, v) -> builder.append(String.format("<li>%s=%s</li>", k, v.getClass().getSimpleName())));
        return builder.toString();
    }

    @GetMapping(value = "/strategy", produces = "text/html;charset=UTF-8")
    public String showContentNegotiationStrategy() {
        Map<String, ContentNegotiationStrategy> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
                ContentNegotiationStrategy.class, true, false);
        ContentNegotiationManager m = (ContentNegotiationManager) matchingBeans.get("mvcContentNegotiationManager");

        if (null != m) {
            List<ContentNegotiationStrategy> strategies = m.getStrategies();
            strategies.forEach(s -> System.out.println(s.getClass().getName()));
        }

        StringBuilder builder = new StringBuilder("ContentNegotiationStrategy接口对象列表: <br/><br/>");
        matchingBeans
                .forEach((k, v) -> builder.append(String.format("<li>%s=%s</li>", k, v.getClass().getSimpleName())));
        return builder.toString();
    }
}
