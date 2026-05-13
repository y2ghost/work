package study.ywork.eshop.thread;

import study.ywork.eshop.request.Request;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 执行请求的工作线程
 */
public class RequestProcessorThread implements Callable<Boolean> {
    // 请求内存队列
    private ArrayBlockingQueue<Request> queue;

    public RequestProcessorThread(ArrayBlockingQueue<Request> queue) {
        this.queue = queue;
    }

    @Override
    public Boolean call() {
        try {
            while (true) {
                // 队列为空的话，take操作会被阻
                Request request = queue.take();
                System.out.println("工作线程处理请求，商品id=" + request.getProductId());
                request.process();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return true;
    }
}
