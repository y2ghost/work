package study.ywork.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/*
 * 演示设置Last-Modified和If-Modified-Since头
 * 演示两种方法:
 * 第一种是WebRequest::checkNotModified检查请求头If-Modified-Since，然后响应设置Last-Modified
 * 第二种就是使用ResponseEntity.BodyBuilder::lastModified方法设置Last-Modified
 */
@Controller
@RequestMapping("/modified")
public class ModifiedController {
    private static final String ETAG = "test-etag";

    @GetMapping(value = "/test-one")
    public String handleTestOne(ServletWebRequest swr) {
        if (swr.checkNotModified(getResourceLastModified())) {
            // 返回 204 代码，空内容，表示未变化
            return null;
        }

        HttpServletResponse res = swr.getResponse();
        if (null != res) {
            res.setHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue());
        }

        return "cache-view";
    }

    @ResponseBody
    @GetMapping(value = "/test-two", produces = "text/html;charset=UTF-8")
    public String handleTestTwo(WebRequest swr) {
        if (swr.checkNotModified(getResourceLastModified())) {
            return null;
        }

        return "<p>响应时间: " + LocalDateTime.now() + "</p><a href=''>测试用例2</a>";
    }

    @ResponseBody
    @GetMapping(value = "/test-three", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> handleTestThree(WebRequest swr) {
        String testBody = "<p>响应时间: " + LocalDateTime.now() + "</p><a href=''>测试用例3</a>";
        return ResponseEntity.ok().lastModified(getResourceLastModified()).body(testBody);
    }

    @ResponseBody
    @GetMapping(value = "/test-four", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> handleTestFour(WebRequest swr) {
        String testBody = "<p>响应时间: " + LocalDateTime.now() + "</p><a href=''>测试用例4</a>";
        return ResponseEntity.ok().eTag(ETAG).body(testBody);
    }

    @ResponseBody
    @GetMapping(value = "/test-five")
    public String handleTestFive(WebRequest swr) {
        if (swr.checkNotModified(ETAG)) {
            return null;
        }

        return "<p>响应时间: " + LocalDateTime.now() + "</p><a href=''>测试用例5</a>";
    }

    private static long getResourceLastModified() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.of(2020, 12, 17, 16, 17, 30), ZoneId.of("GMT"));
        return zdt.toInstant().toEpochMilli();
    }
}
