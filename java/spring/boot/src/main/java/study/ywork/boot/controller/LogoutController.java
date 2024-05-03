package study.ywork.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDateTime;

/*
 * 默认Spring Security的安全支持下，/login页面登录，/logout页面登出
 * 本示例用来演示登出功能
 */
@Controller
public class LogoutController {
    @GetMapping("/do-logout")
    public String doLogout(Model model) {
        model.addAttribute("time", LocalDateTime.now().toString());
        return "logout-page";
    }
}
