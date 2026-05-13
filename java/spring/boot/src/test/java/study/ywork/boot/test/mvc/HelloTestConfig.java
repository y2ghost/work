package study.ywork.boot.test.mvc;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class HelloTestConfig {
    @Bean
    public HelloService helloService() {
        return new HelloService() {
            @Override
            public String getMessage(String name) {
                return "测试你好 " + name;
            }
        };
    }
}
