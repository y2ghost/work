package study.ywork.eshop.request;

import study.ywork.eshop.model.ProductInventory;
import study.ywork.eshop.service.ProductInventoryService;

/**
 * 重新加载商品库存的缓存
 */
public class ProductInventoryCacheRefreshRequest implements Request {
    private Integer productId;
    private ProductInventoryService inventoryService;

    public ProductInventoryCacheRefreshRequest(Integer productId,
                                               ProductInventoryService inventoryService) {
        this.productId = productId;
        this.inventoryService = inventoryService;
    }

    @Override
    public void process() {
        // 从数据库中查询最新的商品库存数量
        ProductInventory inventory = inventoryService.findProductInventory(productId);
        System.out.println("已查询到商品最新的库存数量，商品id=" + productId + ", 商品库存数量=" + inventory.getInventoryCnt());
        // 将最新的商品库存数量，刷新到redis缓存中去
        inventoryService.setProductInventoryCache(inventory);
    }

    public Integer getProductId() {
        return productId;
    }
}
