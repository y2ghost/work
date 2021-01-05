package study.ywork.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;

@Controller
public class MvcController {
    @GetMapping(value = "/mvc", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String handleRequest() {
        return "欢迎来到MVC的世界";
    }

    @GetMapping("/mvc-time")
    public String handleRequest2(ModelMap map) {
        map.addAttribute("time", LocalDateTime.now().toString());
        return "time-page";
    }
}
