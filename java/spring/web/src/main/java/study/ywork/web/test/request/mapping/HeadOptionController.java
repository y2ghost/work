package study.ywork.web.test.request.mapping;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Arrays;

@Controller
public class HeadOptionController {
    @GetMapping("test")
    public HttpEntity<String> handleTestRequest() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.put("test-header", Arrays.asList("test-header-value"));
        HttpEntity<String> responseEntity = new HttpEntity<>("test body", headers);
        System.out.println("HeadOptionController处理完毕");
        return responseEntity;
    }
}
