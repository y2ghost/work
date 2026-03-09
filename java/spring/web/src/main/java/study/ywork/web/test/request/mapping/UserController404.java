package study.ywork.web.test.request.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 没指定具体的请求方法，返回 404 错误
 */
@Controller
@RequestMapping("/users")
public class UserController404 {
    public String handleAllUsersRequest() {
        return "all-users";
    }
}
