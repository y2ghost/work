package study.ywork.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    @GetMapping("/hello-id")
    public String helloId() {
        return "Hello-id!";
    }

    @GetMapping("/hello-key")
    public String helloKey() {
        return "Hello-key!";
    }
}
