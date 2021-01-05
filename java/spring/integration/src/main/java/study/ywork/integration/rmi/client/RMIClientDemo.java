package study.ywork.integration.rmi.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import study.ywork.integration.rmi.order.OrderService;

@Configuration
public class RMIClientDemo {
    @Bean
    public OrderBean orderBean() {
        return new OrderBean();
    }

    @Bean
    public RmiProxyFactoryBean exporter() throws UnknownHostException {
        RmiProxyFactoryBean rpfb = new RmiProxyFactoryBean();
        rpfb.setServiceInterface(OrderService.class);
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        rpfb.setServiceUrl(String.format("rmi://%s:2099/OrderService", hostAddress));
        return rpfb;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RMIClientDemo.class);
        OrderBean bean = ctx.getBean(OrderBean.class);
        bean.placeOrder();
        bean.listOrders();
        ctx.close();
    }
}
