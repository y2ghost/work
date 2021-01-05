package study.ywork.integration.rmi.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
import study.ywork.integration.rmi.order.OrderService;

@Configuration
public class RMIServerDemo {
    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl();
    }

    // 注册RmiServiceExporter类型的Bean，以便导出RMI服务
    @Bean
    public RmiServiceExporter exporter() {
        RmiServiceExporter rse = new RmiServiceExporter();
        rse.setServiceName("OrderService");
        rse.setService(orderService());
        rse.setServiceInterface(OrderService.class);
        rse.setRegistryPort(2099);
        return rse;
    }

    // RMI服务单独的线程处理，所以main函数别直接ctx.close()关闭了
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RMIServerDemo.class);
        if (!ctx.isRunning()) {
            ctx.close();
        }
    }
}
