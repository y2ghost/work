package study.ywork.web.test.request.async;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;

/*
 * Spring异步处理HTTP请求的简单例子
 */
@Controller
public class SimpleController {
    // Spring框架负责线程管理
    @GetMapping("test1")
    public Callable<String> handleTestOne() {
        System.out.println("handleTestOne处理异步请求开始");
        Callable<String> callable = () -> {
            System.out.println("异步任务开始");
            Thread.sleep(2000);
            System.out.println("异步任务结束");
            return "test1 async result";
        };

        System.out.println("处理异步请求结束");
        return callable;
    }

    // 自行进行线程管理处理
    @GetMapping("test2")
    public DeferredResult<String> handleTestTwo() {

        System.out.println("handleTestTwo处理异步请求开始");
        final DeferredResult<String> deferredResult = new DeferredResult<>();

        new Thread(() -> {
            System.out.println("异步任务开始");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println(e);
                Thread.currentThread().interrupt();
            }

            System.out.println("异步任务结束");
            deferredResult.setResult("test2 async result");
        }).start();

        System.out.println("处理异步请求结束");
        return deferredResult;
    }
}
