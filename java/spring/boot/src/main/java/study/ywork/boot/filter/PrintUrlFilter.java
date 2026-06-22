package study.ywork.boot.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/servlet/echo")
public class PrintUrlFilter implements Filter {
    private final String filterName;

    public PrintUrlFilter() {
        this("PrintUrlFilter");
    }

    public PrintUrlFilter(String filterName) {
        this.filterName = filterName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String url = "N/A";
        if (request instanceof HttpServletRequest) {
            url = ((HttpServletRequest) request).getRequestURL().toString();
        }

        System.out.println(String.format("%s处理URL: %s", filterName, url));
        chain.doFilter(request, response);
    }
}
