package study.ywork.web.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ShowBeanNameService {
    private final ApplicationContext context;

    public ShowBeanNameService(ApplicationContext context) {
        this.context = context;
    }

    public String[] getBeanNames() {
        return context.getBeanDefinitionNames();
    }
}
