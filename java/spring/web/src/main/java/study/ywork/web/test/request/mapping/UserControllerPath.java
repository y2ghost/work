package study.ywork.web.test.request.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/**/users")
public class UserControllerPath {
    @GetMapping
    public void handleAllUsersRequest(HttpServletRequest request) {
        System.out.println(request.getRequestURL());
    }
}
