package study.ywork.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
@ResponseBody
public class SessionController {
    private static final String SESSION_KEY = "firstAccessTime";

    @GetMapping(value = "session", produces = "text/plain;charset=UTF-8")
    public String handler(HttpSession httpSession) {
        Object time = httpSession.getAttribute(SESSION_KEY);
        if (time == null) {
            time = LocalDateTime.now();
            httpSession.setAttribute(SESSION_KEY, time);
        }

        return "第一次访问时间: " + time + "\n会话ID: " + httpSession.getId();
    }
}
