package study.ywork.web.test.request.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * 绑定不同的HTTP请求方法（PUT、GET、POST等）
 */
@Controller
@RequestMapping("/users")
public class UserControllerHttpMethods {
    @RequestMapping(value = "{id}", method = {RequestMethod.GET})
    public String handleGet(@PathVariable("id") String userId) {
        System.out.println("处理GET方法");
        return "";
    }

    @RequestMapping(value = "{id}", method = {RequestMethod.DELETE})
    public String handleDelete(@PathVariable("id") String userId) {
        System.out.println("处理DELETE方法");
        return "";
    }
}
