package study.ywork.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 打包成为JAR包，JSP页面，不能放在传统的源代码src/main/webapp/WEB-INF/目录
 * 而是需要支持动态页面的目录，也就是src/main/resources/META-INF/resources/下
 * 当然必须嵌入式的servlet容器支持，此处测试使用的是tomcat-embed-jasper嵌入式依赖库
 * 自定义的favicon.ico图片可以放入任何静态资源目录下，按顺序找到就使用
 */
@RequestMapping("/jsp")
@Controller
public class MyViewController {
    @GetMapping("/my-view")
    public String handler(Model model) {
        model.addAttribute("msg", "JAR包下JSP视图的示例");
        return "myView";
    }
}
