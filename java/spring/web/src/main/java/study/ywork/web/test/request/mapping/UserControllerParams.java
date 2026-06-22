package study.ywork.web.test.request.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserControllerParams {
    @GetMapping(params = "id=4")
    public String handleUserId4() {
        System.out.println("获得参数: id = 4");
        return "";
    }

    @GetMapping(params = "id=10")
    public String handleUserId10() {
        System.out.println("获得参数: id = 10");
        return "";
    }
}
