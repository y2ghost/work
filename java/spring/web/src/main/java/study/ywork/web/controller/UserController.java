package study.ywork.web.controller;

import study.ywork.web.domain.User;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * 用来演示输出不同的MediaType类型的内容给客户端
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUserById(@RequestParam("id") long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("yy");
        user.setEmailAddress("yy@example.com");
        user.setPassword("password");
        return user;
    }

    @GetMapping
    @ResponseBody
    public String getUserStringById(@RequestParam("id") long userId) {
        return "yy, id: " + userId;
    }
}
