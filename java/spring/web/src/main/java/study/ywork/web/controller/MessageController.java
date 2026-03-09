package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.ywork.web.service.ShowBeanNameService;

@Controller
@RequestMapping("/")
public class MessageController {
    private final ShowBeanNameService showBeanNameService;

    public MessageController(ShowBeanNameService showBeanNameService) {
        this.showBeanNameService = showBeanNameService;
    }

    @GetMapping(produces = "text/html; charset=utf-8")
    public String showMessage(Model model) {
        StringBuilder builder = new StringBuilder("欢迎学习Spring WebMvc!!");
        builder.append("<br/>").append("注册的BEAN对象列表<br/><br/>");
        String[] beanNames = showBeanNameService.getBeanNames();

        for (String beanName : beanNames) {
            builder.append("<li>").append(beanName).append("</li>");
        }

        model.addAttribute("msg", builder.toString());
        return "message";
    }
}
