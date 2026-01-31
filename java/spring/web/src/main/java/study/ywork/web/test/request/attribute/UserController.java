package study.ywork.web.test.request.attribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("users")
public class UserController {
    private static final String VIEW = "message";
    @Autowired
    private UserService userService;

    @GetMapping("{userId}")
    public String handleRequestById(@ModelAttribute("user") User user, Model model) {
        System.out.println(user);
        model.addAttribute("msg", "用户: " + user);
        return VIEW;
    }

    @ModelAttribute("user")
    public User getUser(@PathVariable("userId") long userId) {
        return userService.getUserById(userId);
    }
}
