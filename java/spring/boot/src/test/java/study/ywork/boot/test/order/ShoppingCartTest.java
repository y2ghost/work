package study.ywork.boot.test.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * 基本的springboot单元测试示例
 */
@SpringBootTest(classes = OrderDemo.class)
public class ShoppingCartTest {
    @Autowired
    private ShoppingCart shoppingCart;

    @Test
    public void testCheckout() {
        shoppingCart.addItem("Item1", 3);
        shoppingCart.addItem("item2", 5);
        String result = shoppingCart.checkout();
        assertEquals("2 orders placed", result);
    }
}
