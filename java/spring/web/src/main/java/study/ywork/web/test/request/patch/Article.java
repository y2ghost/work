package study.ywork.web.test.request.patch;

public class Article {
    private long id;
    private String content;

    public Article() {
    }

    public Article(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article [id=" + id + ", content=" + content + "]";
    }
}
