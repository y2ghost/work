package study.ywork.boot.test.order;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    public String placeOrders(List<Order> orders) {
        // 模拟订单创建
        return orders.size() + " orders placed";
    }
}
