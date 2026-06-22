package study.ywork.web.test.app;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import study.ywork.web.domain.GlobalStyle;

@Controller
public class AppOneController {
    @Autowired
    private GlobalStyle globalStyle;

    @GetMapping("/**")
    public String appHandler(Model model, HttpServletRequest req) {
        model.addAttribute("style", globalStyle);
        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("msg", "来自APP1的响应");
        return "app-page";
    }

    @PostMapping("/**")
    public String appPostHandler(String fontSize, String background, HttpServletRequest req) {
        globalStyle.setBackground(background);
        globalStyle.setFontSize(fontSize);
        System.out.println(req.getRequestURI());
        return "redirect:/app1/";
    }
}
