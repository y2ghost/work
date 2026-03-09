package study.ywork.config.client.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class ClientBean {
    @Value("${test.greeting}")
    private String msg1;

    @Value("${test.msg}")
    private String msg2;

    @PostConstruct
    public void postConstruct() {
        System.out.println(msg1);
        System.out.println(msg2);
    }
}
