package study.ywork.web.interceptor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
 * Spring可以支持@WebFilter例子
 */
@WebFilter(filterName = "appFilter", urlPatterns = "/servlet/*")
public class AppWebFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        System.out.println("-- AppWebFilter过滤处理 --");
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("URI: " + req.getRequestURI());
        chain.doFilter(request, response);
    }
}
