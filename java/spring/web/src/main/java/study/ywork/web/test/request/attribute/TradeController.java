package study.ywork.web.test.request.attribute;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes("visitor")
@RequestMapping
public class TradeController {
    @GetMapping("/trades/**")
    public String handleRequestById(@ModelAttribute("visitor") Visitor visitor, Model model, HttpServletRequest request,
                                    SessionStatus sessionStatus) {
        String msg = "交易请求，服务页面: " + request.getRequestURI();
        System.out.println(msg);
        model.addAttribute("msg", msg);
        if (request.getRequestURI().endsWith("clear")) {
            sessionStatus.setComplete();
            return "clear-page";
        } else {
            visitor.addPageVisited(request.getRequestURI());
            return "traders-page";
        }
    }

    @ModelAttribute("visitor")
    public Visitor getVisitor(HttpServletRequest request) {
        return new Visitor(request.getRemoteAddr());
    }
}
