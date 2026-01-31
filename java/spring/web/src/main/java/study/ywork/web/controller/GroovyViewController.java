package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/groovy")
public class GroovyViewController {
    @GetMapping
    public String get(Model model) {
        model.addAttribute("msg", "From GroovyViewController");
        return "groovy-view";
    }
}
