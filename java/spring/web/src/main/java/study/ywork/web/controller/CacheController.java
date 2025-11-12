package study.ywork.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/*
 * 演示如何设置: Cache-Control HTTP头
 * 静态资源见MainWebConfig::addResourceHandlers方法示例
 */
@Controller
@RequestMapping("/cache")
public class CacheController {
    @GetMapping("/test-one")
    public String handleTestOnw(HttpServletResponse response) {
        String headerValue = CacheControl.maxAge(10, TimeUnit.SECONDS).getHeaderValue();
        response.addHeader("Cache-Control", headerValue);
        return "cache-view";
    }

    @ResponseBody
    @GetMapping("/test-two")
    public ResponseEntity<String> handle2() {
        CacheControl cacheControl = CacheControl.maxAge(10, TimeUnit.SECONDS);
        String testBody = "<p>响应时间: " + LocalDateTime.now() + "</p><a href=''>测试例子2</a>";
        return ResponseEntity.ok().cacheControl(cacheControl).body(testBody);
    }
}
