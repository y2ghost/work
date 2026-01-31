package study.ywork.boot.component;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/*
 * 监听事件，通过SpringApplication.addListeners()方法添加例子
 * 事件类型：
 * ApplicationStartedEvent
 * ApplicationEnvironmentPreparedEvent
 * ApplicationPreparedEvent
 * ApplicationReadyEvent
 * ApplicationFailedEvent
 *
 * 备注:
 * 也可以通过：src/main/resources/META-INF/spring.factories配置监听器
 */
public class MyApplicationListener implements ApplicationListener<ApplicationEvent> {
    private final String name;

    public MyApplicationListener() {
        this("factory监听器");
    }

    public MyApplicationListener(String name) {
        this.name = name;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println(name + "捕获事件: " + event);
    }
}
