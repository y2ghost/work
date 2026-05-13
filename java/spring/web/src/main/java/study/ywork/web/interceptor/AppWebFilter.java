package study.ywork.web.interceptor;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

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
