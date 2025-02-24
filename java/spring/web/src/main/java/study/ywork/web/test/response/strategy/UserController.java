package study.ywork.web.test.response.strategy;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
    @GetMapping
    @ResponseBody
    public User getUserById(@RequestParam("id") long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("yy");
        user.setEmailAddress("yy@custom.com");
        return user;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getUserPlain(@RequestParam("id") long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("yy");
        user.setEmailAddress("yy@custom.com");
        return user.toString();
    }
}
