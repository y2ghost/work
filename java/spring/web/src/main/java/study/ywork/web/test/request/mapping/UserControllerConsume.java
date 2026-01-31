package study.ywork.web.test.request.mapping;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 不同的请求数据类型，不同的处理方法
 */
@Controller
@RequestMapping("/users")
public class UserControllerConsume {
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public String handleJson(@RequestBody String s) {
        System.out.println("JSON请求: " + s);
        return "";
    }

    @GetMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public String handleXML(@RequestBody String s) {
        System.out.println("XML请求: " + s);
        return "";
    }
}
