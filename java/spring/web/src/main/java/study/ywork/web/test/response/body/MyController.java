package study.ywork.web.test.response.body;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("message")
public class MyController {
    @GetMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String handleRequest(@RequestBody String str) {
        System.out.println("请求消息体: " + str);
        return "<html><body><h1> hello yy</h1></body></html>";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void handleFormRequest(@RequestBody MultiValueMap<String, String> formParams) {
        System.out.println("表单参数: " + formParams);
    }

    @PutMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handleFormPutRequest(@RequestBody MultiValueMap<String, String> formParams) {
        System.out.println("表单参数: " + formParams);
    }
}
