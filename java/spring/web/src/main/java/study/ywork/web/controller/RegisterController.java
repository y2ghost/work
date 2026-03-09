package study.ywork.web.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.ywork.web.domain.User;
import study.ywork.web.service.UserService;

import java.util.regex.Pattern;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private static final String NAME = "name";
    private static final String EMAIL = "emailAddress";
    private static final String PASSWORD = "password";
    private static final String REGISTER = "user-register";
    private static final String REGISTER_DONE = "user-register-done";
    private final UserService userService;
    private final UserValidator userValidator = new UserValidator();

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String handleGetRequest(Model model) {
        model.addAttribute("user", new User());
        return REGISTER;
    }

    /*
     * 采用自定义的验证类进行验证的例子
     */
    @PostMapping(params = "custom=true")
    public String customValidatePostRequest(@ModelAttribute("user") User user, BindingResult result) {
        System.out.println("采用自定义的验证类进行验证的例子");
        userValidator.validate(user, result);
        return validPostRequest(user, result);
    }

    /*
     * 采用Spring框架验证类进行验证的例子
     */
    @PostMapping
    public String validPostRequest(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return REGISTER;
        }

        userService.saveUser(user);
        return REGISTER_DONE;
    }

    /*
     * 自定义验证类
     */
    private static class UserValidator implements Validator {
        private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w\\d._-]+@[\\w\\d.-]+\\.[\\w\\d]{2,6}$");

        @Override
        public boolean supports(Class<?> clazz) {
            return clazz == User.class;
        }

        @Override
        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmpty(errors, NAME, "user.name.empty");
            ValidationUtils.rejectIfEmpty(errors, PASSWORD, "user.password.empty");
            ValidationUtils.rejectIfEmpty(errors, EMAIL, "user.email.empty");

            User user = (User) target;
            if (user.getName() != null && user.getName().length() < 5 || user.getName().length() > 20) {
                errors.rejectValue(NAME, "user.name.size");
            }

            if (user.getPassword() != null && user.getPassword().contains(" ")) {
                errors.rejectValue(PASSWORD, "user.password.space");
            }

            if (user.getPassword() != null && user.getPassword().length() < 5 && user.getPassword().length() > 15) {
                errors.rejectValue(PASSWORD, "user.password.size");
            }

            if (user.getEmailAddress() != null && !EMAIL_REGEX.matcher(user.getEmailAddress()).matches()) {
                errors.rejectValue(EMAIL, "user.email.invalid");
            }
        }
    }
}
