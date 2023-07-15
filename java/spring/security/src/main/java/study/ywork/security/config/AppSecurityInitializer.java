package study.ywork.security.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

// 使用Spring Web MVC则需要继承AbstractSecurityWebApplicationInitializer类
// 以便能够自动初始化Security Filter
public class AppSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    public AppSecurityInitializer() {
        System.out.println("我会被自动加载，从而初始化springSecurityFilterChain");
    }
}
