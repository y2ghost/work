package study.ywork.security.service;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import study.ywork.security.domain.OrderItem;

import java.util.ArrayList;
import java.util.List;

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
    // 此处重复，目的用于演示：@PreAuthorize注解的用法
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<OrderItem> getOrderList() {
        return orderItems;
    }
}
