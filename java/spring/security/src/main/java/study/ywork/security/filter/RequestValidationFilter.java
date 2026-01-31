package study.ywork.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RequestValidationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 只针对/hello-id接口进行过滤
        if ("/hello-id".equals(httpRequest.getRequestURI())) {
            String requestId = httpRequest.getHeader("Request-Id");
            if (requestId == null || requestId.isBlank()) {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
