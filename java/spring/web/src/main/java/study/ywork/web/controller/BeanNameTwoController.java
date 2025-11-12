package study.ywork.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/*
 * 根据Bean的名称作为URL路径映射示例2，在配置BeanConfig里面定义Bean名称
 */
public class BeanNameTwoController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("使用Bean名称作为URL路径示例2");
        return null;
    }
}
