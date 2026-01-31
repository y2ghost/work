package study.ywork.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simple-form")
public class SimpleFormController {
    @PostMapping(produces = "text/plain;charset=UTF-8")
    public String handlePostRequest(String personName, String personAddress) {
        return String.format("简单表单响应结果 名字: %s, 地址: %s", personName, personAddress);
    }
}
