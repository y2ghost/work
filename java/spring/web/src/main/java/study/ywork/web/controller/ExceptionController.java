package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/exception")
public class ExceptionController {
    /*
     * 由CommonControllerAdvice类处理
     */
    @GetMapping
    public void handleRuntimeException() {
        // 仅仅用于测试
        throw new RuntimeException();
    }

    /*
     * 由ShowExceptionResolver类处理
     */
    @GetMapping(params = "null=true")
    public void handleNullPointerException() {
        // 仅仅用于测试
        throw new NullPointerException();
    }

    /*
     * 由本类exceptionHandler方法处理
     */
    @GetMapping(params = "number=true")
    public void handleNumberFormatException() {
        // 仅仅用于测试
        throw new NumberFormatException();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NumberFormatException.class)
    public String exceptionHandler(NumberFormatException ex, Model model) {
        model.addAttribute("exceptionType", ex);
        model.addAttribute("handlerMethod", getHandlerMethod(ex));
        model.addAttribute("resolver", this);
        return "error-page";
    }

    private String getHandlerMethod(Throwable ex) {
        StackTraceElement ele = ex.getStackTrace()[0];
        return String.format("%s#%s()", ele.getClassName(), ele.getMethodName());

    }
}
