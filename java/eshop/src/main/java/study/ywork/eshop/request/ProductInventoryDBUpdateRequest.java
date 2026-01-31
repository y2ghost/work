package study.ywork.eshop.request;

import study.ywork.eshop.model.ProductInventory;
import study.ywork.eshop.service.ProductInventoryService;

public class ProductInventoryDBUpdateRequest implements Request {
    // 商品库存
    private ProductInventory inventory;
    // 商品库存Service
    private ProductInventoryService inventoryService;

    public ProductInventoryDBUpdateRequest(ProductInventory inventory,
                                           ProductInventoryService inventoryService) {
        this.inventory = inventory;
        this.inventoryService = inventoryService;
    }

    @Override
    public void process() {
        System.out.println("数据库更新请求开始执行，商品id=" + inventory.getProductId() + ", 商品库存数量=" + inventory.getInventoryCnt());
        // 先删除缓存在更新数据库是根据业务场景，因为读请求会被阻塞
        inventoryService.removeProductInventoryCache(inventory);
        inventoryService.updateProductInventory(inventory);
    }

    public Integer getProductId() {
        return inventory.getProductId();
    }
}
