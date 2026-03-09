package study.ywork.web.test.request.path;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("users")
public class UserController {
    @GetMapping("/{id}")
    public String handleRequest(@PathVariable("id") String userId, Model map) {
        map.addAttribute("msg", "用户ID: " + userId);
        return "message";

    }

    @GetMapping("/profiles/{userName}")
    public String handleRequest2(@PathVariable("userName") String userName, Model model) {
        model.addAttribute("msg", "用户名称: " + userName);
        return "message";
    }

    @GetMapping("/{id}/posts/{postId}")
    public String handleRequest3(@PathVariable("id") String userId, @PathVariable("postId") String postId,
                                 Model model) {
        model.addAttribute("msg", "用户ID: " + userId + ", 回复ID: " + postId);
        return "message";

    }

    @GetMapping("/{id}/messages/{msgId}")
    public String handleRequest4(@PathVariable Map<String, String> varsMap, Model model) {
        model.addAttribute("msg", varsMap.toString());
        return "message";
    }
}
