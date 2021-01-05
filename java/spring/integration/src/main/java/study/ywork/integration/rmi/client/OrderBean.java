package study.ywork.integration.rmi.client;

import org.springframework.beans.factory.annotation.Autowired;
import study.ywork.integration.rmi.order.Order;
import study.ywork.integration.rmi.order.OrderService;
import java.util.List;

public class OrderBean {
    @Autowired
    private OrderService orderService;

    public void placeOrder() {
        System.out.println("-- 开始订购 --");
        orderService.placeOrder("苹果笔记本", 2);
        orderService.placeOrder("宝骏SUV", 3);
    }

    public void listOrders() {
        System.out.println("-- 从服务中获取订单列表 --");
        List<Order> orderList = orderService.getOrderList();
        System.out.println(orderList);
    }
}
