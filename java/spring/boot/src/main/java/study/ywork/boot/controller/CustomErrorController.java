package study.ywork.boot.controller;

import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/*
 * 演示自定义错误处理器，实现ErrorController接口
 * Spring Boot发现本实现类后，便不再使用默认BasicErrorController类处理错误异常
 * Spring Boot的Mvc 错误处理自动配置参考ErrorMvcAutoConfiguration类
 */
@ConditionalOnProperty(prefix = "server.error.custom", name = "enabled")
@Controller
@RequestMapping("${server.error.path}")
public class CustomErrorController implements ErrorController {
    private ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        // 根据JSR标准，Servlet容器会将标准的错误属性放入request请求中，下面是示例
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        System.out.println("statusCode:" + statusCode);
        System.out.println("exception:" + exception);

        Map<String, Object> model = Collections
            .unmodifiableMap(getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML)));
        final StringBuilder errorDetails = new StringBuilder();
        model.forEach((attribute, value) -> {
            errorDetails.append("<tr><td>")
                .append(attribute)
                .append("</td><td><pre>")
                .append(value)
                .append("</pre></td></tr>");
        });
        return String.format(
            "<html><head><style>td{vertical-align:top;border:solid 1px #666;}</style>"
                + "</head><body><h2>自定义错误处理页面</h2><table>%s</table></body></html>",
            statusCode, exception == null ? "N/A" : exception.getMessage(), errorDetails.toString());
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, ErrorAttributeOptions options) {
        WebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, options);
    }

    private ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request, MediaType mediaType) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
        options = options.including(Include.EXCEPTION);
        options = options.including(Include.STACK_TRACE);
        options = options.including(Include.MESSAGE);
        options = options.including(Include.BINDING_ERRORS);
        return options;
    }
}
