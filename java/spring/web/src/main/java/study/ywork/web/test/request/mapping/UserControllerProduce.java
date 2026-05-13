package study.ywork.web.test.request.mapping;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/users")
public class UserControllerProduce {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String handleJson() {
        System.out.println("获取JSON请求");
        return "{ \"userName\": \"yy\"}";
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody String handleXML() {
        System.out.println("获取XML请求");
        return "<userName>yy</userName>";
    }
}
