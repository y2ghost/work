package study.ywork.eshop.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import study.ywork.eshop.vo.ProductInfo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品控制器
 */
@Controller
public class ProductController {
    @RequestMapping("/getProductInfo")
    @ResponseBody
    public String getProductInfo(Long productId) {
        System.out.println("获取单个商品信息: productId=" + productId);
        ProductInfo info = buildProductInfo(productId);
        return JSON.toJSONString(info);
    }

    @RequestMapping("/getProductInfos")
    @ResponseBody
    public String getProductInfos(String productIds) {
        System.out.println("获取多个商品信息: productIds=" + productIds);
        JSONArray jsonArray = new JSONArray();

        for (String productId : productIds.split(",")) {
            ProductInfo info = buildProductInfo(Long.valueOf(productId));
            jsonArray.add(info);
        }

        return jsonArray.toJSONString();
    }

    private ProductInfo buildProductInfo(Long productId) {
        ProductInfo info = new ProductInfo();
        info.setId(productId);
        info.setName("华为荣耀X30手机");
        info.setPrice(new BigDecimal("5599"));
        info.setPictureList("a.jpg,b.jpg");
        info.setSpecification("荣耀手机规格");
        info.setService("荣耀线下旗舰店");
        info.setColor("红色,白色,黑色");
        info.setSize("5.5寸");
        info.setShopId(1L);
        info.setModifiedTime(LocalDate.of(2023, 4, 2));
        info.setBrandId(10L);
        return info;
    }
}
