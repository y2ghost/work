package study.ywork.web.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import study.ywork.web.test.app.AppOneConfig;
import study.ywork.web.test.app.AppTwoConfig;

/*
 * 加载study.ywork.web.test.app下的类，演示Application Scoped对象的使用方法
 */
public class AppWebInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        registerSpringContext(servletContext, AppOneConfig.class, "dispatcherApp1", "/app1/*");
        registerSpringContext(servletContext, AppTwoConfig.class, "dispatcherApp2", "/app2/*");
    }

    private void registerSpringContext(ServletContext servletContext, Class<?> configClass, String servletName,
                                       String mapping) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(configClass);
        ctx.setServletContext(servletContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet(servletName, new DispatcherServlet(ctx));
        servlet.setLoadOnStartup(1);
        servlet.addMapping(mapping);
    }
}
