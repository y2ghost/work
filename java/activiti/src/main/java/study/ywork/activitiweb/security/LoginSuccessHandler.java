package study.ywork.activitiweb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import study.ywork.activitiweb.util.ResponseVo;
import study.ywork.activitiweb.util.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("loginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ObjectMapper objectMapper;

    public LoginSuccessHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        logger.info("登录成功1");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        Authentication authentication) throws IOException, ServletException {
        logger.info("登录成功2");
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ResponseVo vo = ResponseVo.newResponseVo(ResponseCode.SUCCESS, authentication.getName());
        String json = objectMapper.writeValueAsString(vo);
        httpServletResponse.getWriter().write(json);
    }
}
