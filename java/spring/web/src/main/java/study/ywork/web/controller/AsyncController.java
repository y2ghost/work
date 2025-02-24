package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import java.util.concurrent.Callable;

@RequestMapping("/async")
@Controller
public class AsyncController {
    @GetMapping
    @ResponseBody
    public Callable<String> handleCallable() {
        System.out.println("调用handleCallable开始-线程: " + getThreadName());
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("handleCallable异步任务开始-线程: " + getThreadName());
                Thread.sleep(300);
                System.out.println("handleCallable异步任务结束-线程: " + getThreadName());
                return "async result";
            }
        };

        System.out.println("调用handleCallable结束-线程: " + getThreadName());
        return callable;
    }

    @GetMapping("/deferred")
    @ResponseBody
    public DeferredResult<String> handleDeferred() {
        System.out.println("调用handleDeferred开始-线程: " + getThreadName());
        final DeferredResult<String> deferredResult = new DeferredResult<>();

        new Thread(() -> {
            System.out.println("handleDeferred异步任务开始-线程: " + getThreadName());
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                System.err.println(e);
                Thread.currentThread().interrupt();
            }

            deferredResult.setResult("test async result");
            System.out.println("handleDeferred异步任务结束-线程: " + getThreadName());
        }).start();

        System.out.println("调用handleDeferred结束-线程: " + getThreadName());
        return deferredResult;
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }
}
