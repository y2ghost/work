package study.ywork.boot.controller;

import java.time.LocalTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 打包成为JAR包，JSP页面，不能放在传统的源代码src/main/webapp/WEB-INF/目录
 * 而是需要支持动态页面的目录，也就是src/main/resources/META-INF/resources/下
 * 当然必须嵌入式的servlet容器支持，此处测试使用的是tomcat-embed-jasper嵌入式依赖库
 * 自定义的favicon.ico图片可以放入任何静态资源目录下，按顺序找到就使用
 * 
 * 各个ViewResolver处理器的优先级不一样，导致视图被拦截处理，优先级低的处理器
 * 则无法得到机会处理视图，比如JSP和Thymeleaf两个一起的话，JSP优先级低，无法
 * 得到机会处理，因此共存的办法就是，配置thymeleaf的视图名称模式
 * application.properties中ViewResolver相关配置
 * spring.thymeleaf.view-names=error,thymeleaf*
 * spring.freemarker.view-names=ftl*
 * spring.groovy.template.view-names=groovy*
 *
 */
@RequestMapping("/view")
@Controller
public class ViewController {
    @GetMapping("/jsp-view")
    public String jspView(Model model) {
        model.addAttribute("msg", "JAR包下JSP视图的示例");
        return "my-view";
    }

    @GetMapping("/bean-view")
    public String beanView(Model model) {
        model.addAttribute("msg", "Bean Name视图的示例");
        return "myBeanView";
    }

    @GetMapping("/ftl-view")
    public String ftlView(Model model) {
        model.addAttribute("msg", "FreeMarker视图的示例");
        model.addAttribute("time", LocalTime.now());
        return "ftl-view";
    }

    @GetMapping("/groovy-view")
    public String groovyView(Model model) {
        model.addAttribute("msg", "Groovy视图的示例");
        return "groovy-view";
    }

    @GetMapping("/thymeleaf-view")
    public String thymeleafView(Model model) {
        model.addAttribute("msg", "Thymeleaf视图的示例");
        model.addAttribute("time", LocalTime.now());
        return "thymeleaf-view";
    }
}
