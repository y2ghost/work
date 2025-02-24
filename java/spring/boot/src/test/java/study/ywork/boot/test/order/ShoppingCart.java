package study.ywork.boot.test.order;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ShoppingCart {
    private OrderService orderService;
    private List<Order> orders = new ArrayList<>();

    public ShoppingCart(OrderService orderService) {
        this.orderService = orderService;
    }

    public void addItem(String name, int qty) {
        orders.add(new Order(name, qty));
    }

    public String checkout() {
        String msg = orderService.placeOrders(orders);
        orders.clear();
        return msg;
    }
}

