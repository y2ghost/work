package study.ywork.web.test.request.format;

import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private static final String REGISTER = "user-register";
    private static final String REGISTER_DONE = "user-register-done";
    private UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder("user")
    public void customizeBinding(WebDataBinder binder) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        binder.registerCustomEditor(Date.class, "dateOfBirth", new CustomDateEditor(dateFormatter, true));
    }

    @GetMapping
    public String handleGetRequest(Model model) {
        model.addAttribute("user", new User());
        return REGISTER;
    }

    @PostMapping
    public String validPostRequest(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return REGISTER;
        }

        userService.saveUser(user);
        model.addAttribute("users", userService.getAllUsers());
        return REGISTER_DONE;
    }
}
