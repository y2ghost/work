package study.ywork.web.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * 演示HTTP SESSION生命周期的Bean例子
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VisitorInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int visitCounter;
    private LocalDateTime firstVisitTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVisitCounter() {
        return visitCounter;
    }

    public void setVisitCounter(int visitCounter) {
        this.visitCounter = visitCounter;
    }

    public LocalDateTime getFirstVisitTime() {
        return firstVisitTime;
    }

    public void setFirstVisitTime(LocalDateTime firstVisitTime) {
        this.firstVisitTime = firstVisitTime;
    }

    public void increaseVisitorCounter() {
        visitCounter++;
    }
}
