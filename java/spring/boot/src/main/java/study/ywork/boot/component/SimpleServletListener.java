package study.ywork.boot.component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SimpleServletListener implements ServletContextListener {
    private final String listenerName;

    public SimpleServletListener() {
        this("SimpleServletListener");
    }

    public SimpleServletListener(String listenerName) {
        this.listenerName = listenerName;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(listenerName + ": 上下文初始化");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(listenerName + ": 上下文销毁");
    }
}
