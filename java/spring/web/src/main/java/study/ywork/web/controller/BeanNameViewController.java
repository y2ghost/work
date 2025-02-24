package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalTime;

@Controller
@RequestMapping("/bean-view")
public class BeanNameViewController {
    @GetMapping
    public String get(Model model) {
        model.addAttribute("msg", "消息来自BeanNameViewController");
        model.addAttribute("time", LocalTime.now());
        return "appBeanView";
    }
}
