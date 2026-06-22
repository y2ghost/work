package study.ywork.web.test.request.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 根据HTTP HEADER区分方法
 */
@Controller
@RequestMapping("/users")
public class UserControllerHeader {
    @GetMapping(headers = "id=4")
    public String handleAllUsersRequest() {
        System.out.println("得到头信息： id = 4");
        return "";
    }

    @GetMapping(headers = "id=10")
    public String handleAllUsersRequest2() {
        System.out.println("得到头信息： id = 10");
        return "";
    }
}
