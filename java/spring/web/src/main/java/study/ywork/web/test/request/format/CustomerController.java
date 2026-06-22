package study.ywork.web.test.request.format;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerDataService customerDataService;

    public CustomerController(CustomerDataService customerDataService) {
        this.customerDataService = customerDataService;
    }

    @GetMapping
    public String handleRequest(Model model) {
        model.addAttribute("customerList", customerDataService.getAllUsers());
        return "customers";
    }
}
