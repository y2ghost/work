package study.ywork.eshop.service.impl;

import org.springframework.stereotype.Service;
import study.ywork.eshop.request.ProductInventoryCacheRefreshRequest;
import study.ywork.eshop.request.ProductInventoryDBUpdateRequest;
import study.ywork.eshop.request.Request;
import study.ywork.eshop.request.RequestQueue;
import study.ywork.eshop.service.RequestAsyncProcessService;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

@Service("requestAsyncProcessService")
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {
    @Override
    public void process(Request request) {
        try {
            // 先做读请求的去重
            RequestQueue requestQueue = RequestQueue.getInstance();
            Map<Integer, Boolean> flagMap = requestQueue.getFlagMap();
            Integer productId = request.getProductId();

            if (request instanceof ProductInventoryDBUpdateRequest) {
                // 更新数据的请求将对应的产品标识设置为true
                flagMap.put(productId, true);
            } else if (request instanceof ProductInventoryCacheRefreshRequest) {
                Boolean flag = flagMap.get(productId);

                // 标识不存在，默认设置标识为false
                if (null == flag) {
                    flagMap.put(productId, false);
                }

                // 标识为true，设置标识为false
                if (null != flag && flag) {
                    flagMap.put(productId, false);
                }

                // 读标识存在，过滤读请求
                if (null != flag && !flag) {
                    return;
                }
            }

            // 做请求的路由，根据每个请求的商品id，路由到对应的内存队列中去
            ArrayBlockingQueue<Request> queue = getRoutingQueue(productId);
            // 将请求放入对应的队列中，完成路由操作
            queue.put(request);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 获取路由到的内存队列
     */
    private ArrayBlockingQueue<Request> getRoutingQueue(Integer productId) {
        RequestQueue requestQueue = RequestQueue.getInstance();
        // 先获取productId的hash值
        String key = String.valueOf(productId);
        int h = (null == key) ? 0 : key.hashCode();
        int hash = (h) ^ (h >>> 16);

        // 对hash值取模，将hash值路由到指定的内存队列中，比如内存队列大小8
        // 用内存队列的数量对hash值取模之后，结果一定是在0~7之间
        // 所以任何一个商品id都会被固定路由到同样的一个内存队列中去的
        int index = (requestQueue.queueSize() - 1) & hash;
        System.out.println("路由内存队列，商品id=" + productId + ", 队列索引=" + index);
        return requestQueue.getQueue(index);
    }
}
