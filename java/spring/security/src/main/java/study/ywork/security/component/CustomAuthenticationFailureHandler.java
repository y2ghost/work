package study.ywork.security.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        AuthenticationException e) {
        try {
            httpServletResponse.sendRedirect("/my-error");
            httpServletResponse.setHeader("Demo-Fail-Time", LocalDateTime.now().toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
