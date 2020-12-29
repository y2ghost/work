package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/simple-form")
public class SimpleFormController {
    @PostMapping(produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String handlePostRequest(String personName, String personAddress) {
        return String.format("简单表单响应结果 名字: %s, 地址: %s", personName, personAddress);
    }
}
