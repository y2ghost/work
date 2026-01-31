package study.ywork.integration.web.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping(value = "/{id}", produces = "text/plain;charset=UTF-8")
    public String getUserById(@PathVariable("id") String userId) {
        return "获得用户ID: " + userId;
    }
}
