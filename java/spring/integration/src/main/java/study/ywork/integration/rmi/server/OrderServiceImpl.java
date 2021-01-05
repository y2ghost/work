package study.ywork.integration.rmi.server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import study.ywork.integration.rmi.order.Order;
import study.ywork.integration.rmi.order.OrderService;

public class OrderServiceImpl implements OrderService {
    private List<Order> orders = new ArrayList<>();

    @Override
    public void placeOrder(String item, int quantity) {
        Order order = new Order();
        order.setItem(item);
        order.setQty(quantity);
        order.setOrderDate(LocalDateTime.now());
        System.out.println("已下单: " + order);
        orders.add(order);
    }

    @Override
    public List<Order> getOrderList() {
        return new ArrayList<>(orders);
    }
}
