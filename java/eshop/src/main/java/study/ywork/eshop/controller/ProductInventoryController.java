package study.ywork.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import study.ywork.eshop.model.ProductInventory;
import study.ywork.eshop.request.ProductInventoryCacheRefreshRequest;
import study.ywork.eshop.request.ProductInventoryDBUpdateRequest;
import study.ywork.eshop.request.Request;
import study.ywork.eshop.service.ProductInventoryService;
import study.ywork.eshop.service.RequestAsyncProcessService;
import study.ywork.eshop.vo.Response;

/**
 * 商品库存控制器
 * 针对业务场景
 * 1.一个更新商品库存的请求过来，然后此时会先删除redis中的缓存，然后再进行更新数据写入操作
 * 2.数据写入过程中，我们发送一个商品缓存的读请求，因为此时redis中没有缓存，就会来请求将数据库中最新的数据刷新到缓存中
 * 3.此时读请求会路由到同一个内存队列中，阻塞住，不会执行
 * 4.写请求完成了数据库的更新之后，读请求才会执行
 * 5.读请求执行的时候，会将最新的库存从数据库中查询出来，然后更新到缓存中
 */
@Controller
public class ProductInventoryController {
    private RequestAsyncProcessService asyncProcessService;
    private ProductInventoryService inventoryService;

    public ProductInventoryController(RequestAsyncProcessService asyncProcessService,
                                      ProductInventoryService inventoryService) {
        this.asyncProcessService = asyncProcessService;
        this.inventoryService = inventoryService;

    }

    // 更新商品库存
    @PostMapping("/updateProductInventory")
    @ResponseBody
    public Response updateProductInventory(ProductInventory inventory) {
        System.out.println("接收到更新商品库存的请求，商品id=" + inventory.getProductId() + ", 商品库存数量=" + inventory.getInventoryCnt());
        Response response = null;

        try {
            Request request = new ProductInventoryDBUpdateRequest(inventory, inventoryService);
            asyncProcessService.process(request);
            response = new Response(Response.SUCCESS);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            response = new Response(Response.FAILURE);
        }

        return response;
    }

    // 获取商品库存
    @GetMapping("/getProductInventory")
    @ResponseBody
    public ProductInventory getProductInventory(Integer productId) {
        System.out.println("接收到一个商品库存的读请求，商品id=" + productId);
        ProductInventory inventory = null;

        try {
            Request request = new ProductInventoryCacheRefreshRequest(productId, inventoryService);
            asyncProcessService.process(request);
            // 尝试等待前面有商品库存更新的操作，同时缓存刷新的操作，将最新的数据刷新到缓存中
            long startTime = System.currentTimeMillis();
            long waitTime = 0L;

            // 等待超过200ms没有从缓存中获取到结果
            while (true) {
                // 一般公司里面，面向用户的读请求控制在200ms就可以了
                if (waitTime > 200) {
                    break;
                }

                // 尝试去redis中读取一次商品库存的缓存数据
                inventory = inventoryService.getProductInventoryCache(productId);

                // 如果读取到了结果，那么就返回
                if (inventory != null) {
                    System.out.println("读取到了redis中的库存缓存，商品id=" + inventory.getProductId() + ", 商品库存数量=" + inventory.getInventoryCnt());
                    return inventory;
                } else {
                    // 如果没有读取到结果，那么等待一段时间
                    Thread.sleep(20);
                    long endTime = System.currentTimeMillis();
                    waitTime = endTime - startTime;
                }
            }

            // 直接尝试从数据库中读取数据
            inventory = inventoryService.findProductInventory(productId);
            if (inventory != null) {
                // 将缓存刷新一下
                inventoryService.setProductInventoryCache(inventory);
                return inventory;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return new ProductInventory(productId, -1L);
    }
}
