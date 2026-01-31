package study.ywork.web.test.myweb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {
    @GetMapping("/")
    public String prepareView(Model model) {
        model.addAttribute("msg", "欢迎学习Spring WebMvc!!");
        return "message";
    }
}
