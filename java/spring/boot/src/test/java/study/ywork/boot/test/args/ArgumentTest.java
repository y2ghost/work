package study.ywork.boot.test.args;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Set;

@SpringBootTest(classes = ArgsDemo.class, args = "--app.name=TestApp")
public class ArgumentTest {
    @Value("${app.name}")
    private String appName;
    @Autowired
    ApplicationArguments args;

    @Test
    public void testNormalArgs() {
        System.out.println("app.name: " + appName);
        Assertions.assertThat(appName).isEqualTo("TestApp");
    }

    @Test
    public void testApplicationArgs() {
        Set<String> optionNames = args.getOptionNames();
        System.out.println("optionsNames: " + optionNames);
        Assertions.assertThat(optionNames).containsOnly("app.name");
        List<String> optionValues = args.getOptionValues("app.name");
        System.out.println("optionsValues for app.name: " + optionValues);
        Assertions.assertThat(optionValues).containsOnly("TestApp");
    }
}

