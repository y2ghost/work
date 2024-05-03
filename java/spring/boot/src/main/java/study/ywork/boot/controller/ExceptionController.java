package study.ywork.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.ywork.boot.controller.exception.ForbiddenException;
import study.ywork.boot.controller.exception.NotYetImplemented;

/*
 * 演示错误异常，自定义错误页面显示错误信息
 * JSP和Thymeleaf都存在error视图，可以更改视图解析器的
 * 配置来测试，配置说明如下:
 * spring.thymeleaf.view-names配置项添加error视图名称
 * 那么thymeleaf视图处理器渲染视图，否则默认由JSP处理
 */
@Controller
@RequestMapping("/exception")
public class ExceptionController {
    @GetMapping
    public void runtimeException() {
        throw new RuntimeException("伪装错误：打不开文件");
    }

    @GetMapping("/501")
    public void notYetImplemented() throws NotYetImplemented {
        throw new NotYetImplemented("请求未实现");
    }

    @GetMapping("/403")
    public void forbiddenException() throws ForbiddenException {
        throw new ForbiddenException("请求禁止访问");
    }
}
