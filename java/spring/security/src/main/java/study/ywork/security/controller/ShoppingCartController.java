package study.ywork.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.ywork.security.domain.OrderItem;
import study.ywork.security.service.ShoppingCartService;

@RequestMapping("/order")
@Controller
public class ShoppingCartController {
    private ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public String placeOrderPage(Model model) {
        addUserInfo(model);
        return "place-order";
    }

    // 认证出错后，内部转发，方法需要支持GET,POST等
    // 所以此处需要支持所有的HTTP方法
    @RequestMapping("/noaccess")
    public String noAccess(Model model) {
        addUserInfo(model);
        return "no-access";
    }

    @PostMapping("/buy")
    public String addOrderItem(OrderItem orderItem, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        orderItem.setCustomer(auth.getName());
        shoppingCartService.placeOrder(orderItem);
        model.addAttribute("status", "订单已购买," + orderItem);
        addUserInfo(model);
        return "order-status";
    }

    // 非ADMIN角色用户请求该方法，无权限
    @GetMapping("/list")
    public String getOrderItemList(Model model) {
        addUserInfo(model);
        model.addAttribute("orderList", shoppingCartService.getOrderList().toString());
        return "order-list";
    }

    private void addUserInfo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("userInfo", String.format("%s [%s]", auth.getName(), auth.getAuthorities()));
    }
}
