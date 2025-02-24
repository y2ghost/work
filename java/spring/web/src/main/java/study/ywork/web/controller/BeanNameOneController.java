package study.ywork.web.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;

/*
 * 根据Bean的名称作为URL路径映射，可被@ComponentScan注解扫描
 */
@Controller("/bean-name-one")
public class BeanNameOneController implements HttpRequestHandler {
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("使用Bean名称作为URL路径示例1");
    }
}
