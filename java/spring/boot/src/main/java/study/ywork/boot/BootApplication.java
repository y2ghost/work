package study.ywork.boot;

import java.util.List;
import java.util.Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;

/*
 * 使用Gradle工具构建，运行程序: gradle bootRun
 * 可以自定Banner文本内容，默认文件src/main/resources/banner.txt
 * 也可以关闭Banner显示: app.setBannerMode(Banner.Mode.OFF)
 */
@ServletComponentScan
@SpringBootApplication
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BootApplication.class);
        // 不打印启动消息
        app.setLogStartupInfo(false);
        Properties p = new Properties();
        p.setProperty("spring.banner.location", "classpath:my-banner.txt");
        // 按需去掉注释
        // p.setProperty("logging.level.org.springframework", "DEBUG")
        app.setDefaultProperties(p);
        // 打印获取的客户端自动配置包
        ConfigurableApplicationContext ctx = app.run(args);
        List<String> autoPackages = AutoConfigurationPackages.get(ctx);
        System.out.println("AutoPackage: " + autoPackages);
    }
}
