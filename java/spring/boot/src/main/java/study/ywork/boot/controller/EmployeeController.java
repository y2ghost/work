package study.ywork.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.ywork.boot.domain.Employee;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/employee")
@Controller
public class EmployeeController {
    private static List<Employee> employeeList = new ArrayList<>();

    @PostMapping
    public String handlePostRequest(@Valid Employee employee, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "thymeleaf-employee-form";
        }

        employeeList.add(employee);
        return "redirect:/employee/list";
    }

    @GetMapping
    public String handleGetRequest(Employee employee) {
        return "thymeleaf-employee-form";
    }

    @GetMapping("/list")
    public String handleGetRequest(Model model) {
        model.addAttribute("employees", employeeList);
        return "thymeleaf-employee-view";
    }
}
