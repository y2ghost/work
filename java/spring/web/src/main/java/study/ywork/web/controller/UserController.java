package study.ywork.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.web.domain.User;

/*
 * 用来演示输出不同的MediaType类型的内容给客户端
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUserById(@RequestParam("id") long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("yy");
        user.setEmailAddress("yy@example.com");
        user.setPassword("password");
        return user;
    }

    @GetMapping
    public String getUserStringById(@RequestParam("id") long userId) {
        return "yy, id: " + userId;
    }
}
