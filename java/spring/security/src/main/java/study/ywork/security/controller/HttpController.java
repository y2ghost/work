package study.ywork.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HttpController {
    @GetMapping("/http/**")
    public String handleRequest2(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("uri", request.getRequestURI())
            .addAttribute("user", auth.getName())
            .addAttribute("roles", auth.getAuthorities());
        return "http-page";
    }
}
