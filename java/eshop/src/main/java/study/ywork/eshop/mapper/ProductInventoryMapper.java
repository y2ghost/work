package study.ywork.eshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import study.ywork.eshop.model.ProductInventory;

@Mapper
public interface ProductInventoryMapper {
    // 更新库存数量
    void updateProductInventory(ProductInventory inventory);

    // 根据商品id查询商品库存信息
    ProductInventory findProductInventory(@Param("productId") Integer productId);
}
