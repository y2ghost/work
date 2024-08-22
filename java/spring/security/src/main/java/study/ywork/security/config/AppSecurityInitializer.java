package study.ywork.security.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class AppSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    public AppSecurityInitializer() {
        super(AppSecurityConfig.class);
        System.out.println("我会被自动加载，从而初始化springSecurityFilterChain");
    }
}
