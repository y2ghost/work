package study.ywork.activitiweb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import study.ywork.activitiweb.util.ResponseVo;
import study.ywork.activitiweb.util.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("loginFailureHandler")
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ObjectMapper objectMapper;

    public LoginFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        AuthenticationException e) throws IOException, ServletException {
        logger.info("登录失败");
        httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ResponseVo vo = ResponseVo.newResponseVo(ResponseCode.ERROR, "登录失败：" + e.getMessage());
        String json = objectMapper.writeValueAsString(vo);
        httpServletResponse.getWriter().write(json);
    }
}
