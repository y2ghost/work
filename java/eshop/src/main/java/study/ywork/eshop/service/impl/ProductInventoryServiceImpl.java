package study.ywork.eshop.service.impl;

import org.springframework.stereotype.Service;
import study.ywork.eshop.dao.RedisDAO;
import study.ywork.eshop.mapper.ProductInventoryMapper;
import study.ywork.eshop.model.ProductInventory;
import study.ywork.eshop.service.ProductInventoryService;

@Service("productInventoryService")
public class ProductInventoryServiceImpl implements ProductInventoryService {
    private ProductInventoryMapper inventoryMapper;
    private RedisDAO redisDAO;

    public ProductInventoryServiceImpl(ProductInventoryMapper inventoryMapper,
                                       RedisDAO redisDAO) {
        this.inventoryMapper = inventoryMapper;
        this.redisDAO = redisDAO;
    }

    @Override
    public void updateProductInventory(ProductInventory inventory) {
        inventoryMapper.updateProductInventory(inventory);
        System.out.println("已修改数据库中的库存，商品id=" + inventory.getProductId() + ", 商品库存数量=" + inventory.getInventoryCnt());
    }

    @Override
    public void removeProductInventoryCache(ProductInventory inventory) {
        String key = "product:inventory:" + inventory.getProductId();
        redisDAO.delete(key);
        System.out.println("已删除redis中的缓存，key=" + key);
    }

    public ProductInventory findProductInventory(Integer productId) {
        return inventoryMapper.findProductInventory(productId);
    }

    public void setProductInventoryCache(ProductInventory inventory) {
        String key = "product:inventory:" + inventory.getProductId();
        redisDAO.set(key, String.valueOf(inventory.getInventoryCnt()));
        System.out.println("已更新商品库存的缓存，商品id=" + inventory.getProductId() + ", 商品库存数量=" + inventory.getInventoryCnt() + ", key=" + key);
    }

    public ProductInventory getProductInventoryCache(Integer productId) {
        String key = "product:inventory:" + productId;
        String result = redisDAO.get(key);

        if (result != null && !"".equals(result)) {
            try {
                Long inventoryCnt = Long.valueOf(result);
                return new ProductInventory(productId, inventoryCnt);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return null;
    }
}
