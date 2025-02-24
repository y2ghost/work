package study.ywork.web.test.response.body;

import org.springframework.util.MultiValueMap;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HttpEntityController {
    @PostMapping
    public ResponseEntity<String> handleRequest(RequestEntity<String> strEntity) {
        System.out.println("请求消息体: " + strEntity.getBody());
        HttpHeaders headers = strEntity.getHeaders();
        System.out.println("请求消息头: " + headers);
        return new ResponseEntity<>("yy response body", HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<String> handleUserRequest(RequestEntity<HttpEntityUser> userEntity) {
        HttpEntityUser user = userEntity.getBody();
        System.out.println("请求消息体: " + user);
        System.out.println("请求消息头: " + userEntity.getHeaders());
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.put("Cache-Control", Arrays.asList("max-age=3600"));
        return new ResponseEntity<>("yy response body", headers, HttpStatus.OK);
    }
}
