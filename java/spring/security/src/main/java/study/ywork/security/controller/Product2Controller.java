package study.ywork.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.security.domain.Product;
import study.ywork.security.service.ProductService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Product2Controller {
    private final ProductService productService;

    public Product2Controller(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/sell")
    public List<Product> sellProduct() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("beer", "nikolai"));
        products.add(new Product("candy", "nikolai"));
        products.add(new Product("chocolate", "julien"));
        return productService.sellProducts(products);
    }
    
    @GetMapping("/find")
    public List<Product> findProducts() {
        return productService.findProducts();
    }
}
