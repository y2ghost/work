package study.ywork.web.test.request.path;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 此处没有明确正则表达会引起二义性
 */
@Controller
@RequestMapping("/employees")
public class EmployeeController {
    @GetMapping("{id}")
    public String handleRequest(@PathVariable("id") String userId, Model model) {
        model.addAttribute("msg", "雇员ID: " + userId);
        return "message";

    }

    @GetMapping("{employeeName}")
    public String handleRequest2(@PathVariable("employeeName") String userName, Model model) {
        model.addAttribute("msg", "雇员名字: " + userName);
        return "message";
    }
}
