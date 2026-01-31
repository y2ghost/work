package study.ywork.web.test.request.attribute;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/users")
public class ModelController {
    private static final String VIEW = "message";
    private int counter = 0;

    @GetMapping
    public String handleRequest(Model model) {
        model.addAttribute("msg", "处理用户的请求");
        System.out.println(model);
        return VIEW;
    }

    @GetMapping("{id}")
    public String handleRequestById(@PathVariable("id") String id, Model model) {
        model.addAttribute("msg", "处理用户ID的请求: " + id);
        System.out.println(model);
        return VIEW;
    }

    @ModelAttribute("time")
    public LocalDateTime getRequestTime() {
        return LocalDateTime.now();
    }

    @ModelAttribute("visits")
    public int getRequestCount() {
        return ++counter;
    }

    @ModelAttribute("querier")
    public void populateIds(@RequestParam(value = "querier", required = false) String querier, Model model) {
        model.addAttribute("querier", null == querier ? "quest" : querier);
    }
}
