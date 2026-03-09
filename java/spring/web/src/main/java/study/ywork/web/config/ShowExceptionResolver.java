package study.ywork.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.Properties;

/*
 * 用来捕获处理器函数发生异常行为时自定义异常处理逻辑，记住处理不了其他地方发送的异常
 * 委托SimpleMappingExceptionResolver类处理
 */
public class ShowExceptionResolver implements HandlerExceptionResolver, Ordered {
    private final SimpleMappingExceptionResolver smeResolver;

    public ShowExceptionResolver() {
        this.smeResolver = new SimpleMappingExceptionResolver();
        Properties p = new Properties();
        p.setProperty(NullPointerException.class.getName(), "null-page");
        smeResolver.setExceptionMappings(p);
        smeResolver.addStatusCode("null-page", 404);
        smeResolver.setDefaultErrorView("error-page");
        smeResolver.setDefaultStatusCode(400);
    }

    /*
     * 为了测试异常处链，本方法只处理空指针异常
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        Class<?> cls = ex.getClass();
        if (cls != NullPointerException.class) {
            return null;
        }

        ModelAndView model = smeResolver.resolveException(request, response, handler, ex);
        if (null != model) {
            model.addObject("exceptionType", ex);
            model.addObject("handlerMethod", handler);
            model.addObject("resolver", this);
        }

        return model;
    }

    /*
     * 默认的Spring异常处理类，排序为0，小于它便可以优先本类处理
     */
    @Override
    public int getOrder() {
        return (-1);
    }
}
