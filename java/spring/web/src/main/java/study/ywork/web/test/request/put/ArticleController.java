package study.ywork.web.test.request.put;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/articles")
public class ArticleController {
    // 处理JSON数据类型:application/json
    @PutMapping("/{id}")
    @ResponseBody
    public String createNewArticle(@RequestBody Article article) {
        System.out.println("通过JSON创建文章: " + article);
        return "Article created.";
    }

    // 处理表单数据类型: application/x-www-form-urlencoded
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public String createNewArticleWithFormParams(@RequestBody MultiValueMap<String, String> formParams) {
        System.out.println("通过表单创建文章: " + formParams);
        return "Article created.";
    }
}
