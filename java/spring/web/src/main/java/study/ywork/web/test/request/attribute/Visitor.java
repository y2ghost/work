package study.ywork.web.test.request.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 演示@SessionAttributes注解的使用，作用是将指定的 Model 的键值对保存在HTTP Session里面
 */
public class Visitor implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String ip;
    private final List<String> pageVisited = new ArrayList<>();

    public Visitor(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void addPageVisited(String page) {
        pageVisited.add(page);
    }

    public List<String> getPagesVisited() {
        return pageVisited;
    }
}
