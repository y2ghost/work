package study.ywork.web.test.request.path;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 使用互斥正则表达避免路径出现二义性 exception
 */
@Controller
@RequestMapping("/dept")
public class DeptController {
    @GetMapping("{id:[0-9]+}")
    public String handleRequest(@PathVariable("id") String userId, Model model) {
        model.addAttribute("msg", "用户ID: " + userId);
        return "message";
    }

    @GetMapping("{name:[a-zA-Z]+}")
    public String handleRequest2(@PathVariable("name") String deptName, Model model) {
        model.addAttribute("msg", "部门名称: " + deptName);
        return "message";
    }
}
