package study.ywork.security.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import study.ywork.security.domain.OrderItem;

@Service
public class ShoppingCartService {
    private List<OrderItem> orderItems = new ArrayList<>();

    @Secured("ROLE_USER")
    public int placeOrder(OrderItem order) {
        int id = orderItems.size() + 1;
        order.setId(id);
        orderItems.add(order);
        return id;
    }

    // JSR-250风格，和@Secured注解功能相同
    @RolesAllowed("ROLE_ADMIN")
    public List<OrderItem> getOrderList() {
        return orderItems;
    }
}
