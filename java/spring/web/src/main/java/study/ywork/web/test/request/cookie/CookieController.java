package study.ywork.web.test.request.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
public class CookieController {
    private static final String VIEW = "message";

    @GetMapping("test")
    public String handleTestRequest(Model model, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).forEach(c -> System.out.println(c.getName() + "=" + c.getValue()));
        }

        Cookie newCookie = new Cookie("testCookie", "testCookieValue");
        newCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(newCookie);
        model.addAttribute("msg", "测试Cookie");
        return VIEW;
    }

    @GetMapping("test2")
    public String handleRequest(
            @CookieValue(value = "testCookie", defaultValue = "defaultCookieValue") String cookieValue, Model model) {
        System.out.println(cookieValue);
        model.addAttribute("cookieValue", cookieValue);
        return VIEW;
    }
}
