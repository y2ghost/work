package study.ywork.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDateTime;

/*
 * 演示@RequestAttribute注解的使用方法，该属性由CounterInterceptor类对象设置
 * 演示@SessionAttribute注解的使用方法，该属性由CounterInterceptor类对象设置
 */
@RestController
public class CounterController {
    @GetMapping(value = "counter", produces = "text/plain;charset=UTF-8")
    public String handle(@RequestAttribute("visitorCounter") Integer counter,
                         @SessionAttribute(name = "startTime") LocalDateTime startDateTime) {
        return String.format("访问次数: %d, 首次访问: %s", counter, startDateTime);
    }
}
