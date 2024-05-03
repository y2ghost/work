package study.ywork.web.test.request.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserControllerRegex {
    @GetMapping("{id:[0-9]+}")
    public String handleAllUsersRequest(Model model, @PathVariable("id") String id) {
        System.out.println("user id数字 " + id);
        model.addAttribute("userId", id);
        return "all-users";
    }

    @GetMapping("{id:[a-z]+}")
    public String handleAllUsersRequest2(Model model, @PathVariable("id") String id) {
        System.out.println("user id字符 " + id);
        model.addAttribute("userStringId", id);
        return "all-users";
    }
}
