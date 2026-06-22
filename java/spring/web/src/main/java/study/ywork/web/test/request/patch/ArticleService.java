package study.ywork.web.test.request.patch;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

public interface ArticleService {
    Article getArticleById(long id);

    void updateArticle(long id, String content);

    @Service
    class DefaultArticleService implements ArticleService {
        private final Map<Long, Article> articleMap = new HashMap<>();

        @PostConstruct
        private void postConstruct() {
            Article article = new Article(1, "test data");
            articleMap.put((long) 1, article);
        }

        @Override
        public Article getArticleById(long id) {
            return articleMap.get(id);
        }

        @Override
        public void updateArticle(long id, String content) {
            if (!articleMap.containsKey(id)) {
                throw new IllegalArgumentException("没有找到文章属于ID: " + id);
            }

            Article article = articleMap.get(id);
            article.setContent(content);
        }
    }
}
