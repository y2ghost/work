package study.ywork.security.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MainWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { MainWebConfig.class };

    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[0];
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}

// 如果不使用Spring Web MVC，则可以使用如下类的方式启用spring security的支持
// public class AppInitializer extends AbstractSecurityWebApplicationInitializer {
//     public AppInitializer() {
//         super(AppSecurityConfig.class);
//     }
// }
