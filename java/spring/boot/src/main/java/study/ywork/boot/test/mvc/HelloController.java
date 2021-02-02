package study.ywork.boot.test.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    private HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping(value = "/")
    public String sayReturnView(String name, Model model) {
        model.addAttribute("msg", helloService.getMessage(name));
        return "hello-page";
    }

    @GetMapping(value = "/body")
    @ResponseBody
    public String sayReturnBody(String name) {
        return helloService.getMessage(name);
    }
}

