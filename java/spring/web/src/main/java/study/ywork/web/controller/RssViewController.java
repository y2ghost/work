package study.ywork.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.ywork.web.domain.FeedEntry;
import study.ywork.web.domain.FeedInfo;

@Controller
@RequestMapping("/rss")
public class RssViewController {
    @GetMapping
    public String get(Model model) {
        model.addAttribute("feedInfo", getFeedInfo());
        return "appRssView";
    }

    private static FeedInfo getFeedInfo() {
        FeedInfo feedInfo = new FeedInfo();
        feedInfo.setTitle("Rss Example");
        feedInfo.setLink("http://www.example.com");
        feedInfo.setDesc("Learn RSS FEED Spring View");

        List<FeedEntry> list = new ArrayList<>();
        Date now = new Date();
        list.add(createFeedEntry("Spring Core", "spring-core", "Spring Core Tutorials", now));
        list.add(createFeedEntry("Java EE", "java-ee-", "Java EE Tutorials", now));
        list.add(createFeedEntry("Java Core", "java-core", "Core Java Tutorials", now));
        feedInfo.setFeedEntries(list);
        return feedInfo;
    }

    private static FeedEntry createFeedEntry(String title, String uri, String content, Date date) {
        FeedEntry fe = new FeedEntry();
        fe.setTitle(title);
        fe.setLink("http://www.example.com/tutorials/" + uri);
        fe.setContent(content);
        fe.setDate(date);
        return fe;
    }
}
