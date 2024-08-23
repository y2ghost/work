package study.ywork.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuthenticationLoggingFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        String requestId = httpRequest.getHeader("Request-Id");
        logger.info("Successfully authenticated request with id {}", requestId);
        filterChain.doFilter(request, response);
    }
}
