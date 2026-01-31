package study.ywork.boot.test.mvc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/*
 * 自定义测试配置类示例
 */
@SpringBootTest
@Import(HelloTestConfig.class)
public class HelloTestConfigTest {
    @Autowired
    private HelloService helloService;

    @Test
    public void testSayHi() {
        String message = helloService.getMessage("yy");
        System.out.println(message);
        Assertions.assertEquals(message, "测试你好 yy");
    }
}
