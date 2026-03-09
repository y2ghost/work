package study.ywork.eshop.thread;

import study.ywork.eshop.request.Request;
import study.ywork.eshop.request.RequestQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请求处理线程池，采用单例模式
 */
public class RequestProcessorThreadPool {
    // 可以考虑通过配置设置线程池大小
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public RequestProcessorThreadPool() {
        RequestQueue requestQueue = RequestQueue.getInstance();

        for (int i = 0; i < 10; i++) {
            ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<>(100);
            requestQueue.addQueue(queue);
            threadPool.submit(new RequestProcessorThread(queue));
        }
    }

    /**
     * 静态内部类的方式，去初始化单例
     */
    private static class Singleton {
        private static RequestProcessorThreadPool instance;

        static {
            instance = new RequestProcessorThreadPool();
        }

        public static RequestProcessorThreadPool getInstance() {
            return instance;
        }

    }

    /**
     * 内部类的初始化，JVM的机制去保证多线程并发安全
     */
    public static RequestProcessorThreadPool getInstance() {
        return Singleton.getInstance();
    }

    /**
     * 初始化的便捷方法
     */
    public static void init() {
        getInstance();
    }
}
