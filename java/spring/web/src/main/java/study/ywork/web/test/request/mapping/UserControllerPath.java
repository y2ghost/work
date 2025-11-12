package study.ywork.web.test.request.mapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserControllerPath {
    @RequestMapping("/users/**")
    public void handleAllUsersRequest(HttpServletRequest request) {
        System.out.println(request.getRequestURL());
    }
}
