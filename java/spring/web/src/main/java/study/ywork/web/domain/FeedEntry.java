package study.ywork.web.domain;

import java.util.Date;

public class FeedEntry {
    String title;
    String link;
    String content;
    Date date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FeedEntry [title=" + title + ", link=" + link + ", content=" + content + ", date=" + date + "]";
    }
}
