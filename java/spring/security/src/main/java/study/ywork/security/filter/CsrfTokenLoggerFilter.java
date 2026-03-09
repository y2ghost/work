package study.ywork.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;

public class CsrfTokenLoggerFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(CsrfTokenLoggerFilter.class);

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        logger.info("CSRF token " + token.getToken());
        filterChain.doFilter(request, response);
    }
}
