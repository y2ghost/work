package study.ywork.boot.controller;

import java.util.Map;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ViewResolver;

/*
 * 打印ViewResolver接口对象注册信息
 */
@Controller
@RequestMapping("/view-resolvers")
public class ShowViewResolverController {
    private ApplicationContext context;

    public ShowViewResolverController(ApplicationContext context) {
        this.context = context;
    }

    @GetMapping(value = "/show", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String showViewResolvers() {
        Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
            ViewResolver.class, true, false);
        StringBuilder builder = new StringBuilder("ViewResolver接口对象列表: <br/><br/>");
        matchingBeans.forEach((k, v) -> builder.append(String.format("<li>%s=%s(%d)</li>",
            k, v.getClass().getSimpleName(), ((Ordered) v).getOrder())));
        return builder.toString();
    }
}
