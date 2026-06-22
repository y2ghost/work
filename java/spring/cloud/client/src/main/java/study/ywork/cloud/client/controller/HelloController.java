package study.ywork.cloud.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import study.ywork.cloud.client.domain.Hello;

@Controller
public class HelloController {
    private RestTemplate restTemplate;

    public HelloController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String handleRequest(Model model) {
        // 和 hello-rest 服务交互
        Hello helloObject = restTemplate.getForObject("http://hello-rest/hello", Hello.class);
        model.addAttribute("msg", helloObject.getMessage());
        model.addAttribute("time", LocalDateTime.now());
        return "hello-page";
    }
}
