package study.ywork.web.test.request.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/*
 * 演示如下注解的使用方法
 * @GetMapping
 * @PostMapping
 * @PutMapping
 * @DeleteMapping
 * @PatchMapping
 */
@Controller
public class VariantController {
    @GetMapping("test")
    public String handleGetRequest() {
        return "GetMapping-view";
    }

    @PostMapping("test")
    public String handlePostRequest() {
        return "PostMapping-view";
    }

    @PutMapping("test")
    public String handlePutRequest() {
        return "PutMapping-view";
    }

    @DeleteMapping("test")
    public String handleDeleteRequest() {
        return "DeleteMapping-view";
    }

    @PatchMapping("test")
    public String handlePatchRequest() {
        return "PatchMapping-view";
    }
}
