package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import study.ywork.web.domain.VisitorInfo;

import java.time.LocalDateTime;

/*
 * 演示使用SESSION级别的Bean对象例子
 */
@Controller
public class VisitorController {
    private final VisitorInfo visitorInfo;

    public VisitorController(VisitorInfo visitorInfo) {
        this.visitorInfo = visitorInfo;
    }

    @GetMapping("/visitor")
    public String appHandler(Model model) {
        if (null == visitorInfo.getName()) {
            return "visitor-main";
        }

        model.addAttribute("visitor", visitorInfo);
        visitorInfo.increaseVisitorCounter();
        return "visitor-info";
    }

    @PostMapping("/visitor-create")
    public String visitorHandler(String name) {
        visitorInfo.setName(name);
        visitorInfo.setFirstVisitTime(LocalDateTime.now());
        return "redirect:/visitor";
    }
}
