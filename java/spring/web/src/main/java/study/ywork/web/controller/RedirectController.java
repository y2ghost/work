package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import java.util.Map;

/*
 * 重定向的简单例子
 */
@Controller
public class RedirectController {
    private static final String VIEW = "message";

    @GetMapping("redirect1")
    public RedirectView handleRedirect1(Model model) {
        model.addAttribute("city", "yueyang");
        RedirectView rv = new RedirectView();
        rv.setUrl("redirect2");
        return rv;
    }

    @GetMapping("redirect2")
    public String handleRedirect2(@RequestParam Map<String, String> params, Model model) {
        model.addAttribute("msg", params);
        return VIEW;
    }

    @GetMapping("redirect3/{id}")
    public String handleRedirect3(Model model) {
        model.addAttribute("job", "programmer");
        model.addAttribute("jobPath", "dev");
        return "redirect:/redirect4/{jobPath}/{id}";
    }

    @GetMapping("redirect4/{jobPath}/{id}")
    public String handleRedirect4(@PathVariable("jobPath") String jobPath, @PathVariable("id") String id,
        @RequestParam("job") String job, Model model) {
        String msg = String.format("job:%s, path:%s, id:%s", job, jobPath, id);
        model.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping("forward")
    public String handleForward() {
        return "forward:/redirect4/forward/666?job=test";
    }
}
