package study.ywork.web.test.request.patch;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    private ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 处理JSON数据类型:application/json
    @PatchMapping("/{id}")
    public String patchArticle(@RequestBody Article article) {
        System.out.println("通过JSON创建文章: " + article);
        articleService.updateArticle(article.getId(), article.getContent());
        return "Article updated with content: " + article.getContent();
    }

    // 处理表单数据类型: application/x-www-form-urlencoded
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String patchArticle(@RequestBody MultiValueMap<String, String> formParams) {
        System.out.println("通过表单创建文章: " + formParams);
        long id = Long.parseLong(formParams.getFirst("id"));
        String content = formParams.getFirst("content");
        articleService.updateArticle(id, content);
        return "Article updated with content: " + content;
    }
}
