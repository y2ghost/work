package study.ywork.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticationLoggingFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var httpRequest = (HttpServletRequest) request;
        String requestId = httpRequest.getHeader("Request-Id");
        if (null != requestId) {
            logger.info("Successfully authenticated request with id {}", requestId);
        }
        
        filterChain.doFilter(request, response);
    }
}
