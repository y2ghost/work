package study.ywork.web.component;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * 处理全局异常错误
 */
@ControllerAdvice
public class CommonControllerAdvice {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
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
