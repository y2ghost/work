package study.ywork.eshop.service;

import study.ywork.eshop.model.ProductInventory;

/**
 * 商品库存Service接口
 */
public interface ProductInventoryService {
    // 更新商品库存
    void updateProductInventory(ProductInventory inventory);

    // 删除Redis中的商品库存的缓存
    void removeProductInventoryCache(ProductInventory inventory);

    // 根据商品id查询商品库存
    ProductInventory findProductInventory(Integer productId);

    // 设置商品库存的缓存
    void setProductInventoryCache(ProductInventory inventory);

    // 获取商品库存的缓存
    ProductInventory getProductInventoryCache(Integer productId);
}
