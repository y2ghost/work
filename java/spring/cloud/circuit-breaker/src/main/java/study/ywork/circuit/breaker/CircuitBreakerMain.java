package study.ywork.circuit.breaker;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.ConfigurableApplicationContext;
import com.netflix.hystrix.strategy.HystrixPlugins;
import study.ywork.circuit.breaker.notifier.MyNotifier;
import study.ywork.circuit.breaker.service.MyService;

/*
 * 启动断路器熔断机制默认条件如下：
 * 带有@HystrixCommand注解的函数调用次数超过限制: circuitBreaker.requestVolumeThreshold(20)
 * 上面的函数处理超过一定的时间限制: metrics.rollingStats.timeInMilliseconds(10 seconds)
 * 上面的函数调用失败率超过配置的值: circuitBreaker.errorThresholdPercentage(50%)
 * 失败的原因有函数返回异常，或者函数调用其他服务响应超时(execution.isolation.thread.timeoutInMilliseconds(1s))
 * 
 * 熔断机制生效后，不会转发调用到后端服务，直接返回错误，从而不阻塞服务，一般默认生效5秒
 * 配置: circuitBreaker.sleepWindowInMilliseconds
 */
@SpringBootApplication
@EnableCircuitBreaker
public class CircuitBreakerMain {
    public static void main(String[] args) throws InterruptedException {
        HystrixPlugins.getInstance().registerEventNotifier(new MyNotifier());
        ConfigurableApplicationContext ctx = SpringApplication.run(CircuitBreakerMain.class, args);
        MyService myService = ctx.getBean(MyService.class);
        System.out.println("-- 调用 doSomething(2) --");
        myService.doSomething(2);
        System.out.println("-- 调用 doSomething(0) --");
        myService.doSomething(0);
        System.out.println("-- 调用 doSomething(5) --");
        myService.doSomething(5);
        System.out.println("-- 调用 doSomething2(2) --");
        myService.doSomething2(2);

        System.out.println("-- 调用 doSomething(1) 40次 --");
        int n = 40;
        for (int i = 0; i < n; i++) {
            myService.doSomething(i < (n * 0.6) ? 0 : 2);
            TimeUnit.MILLISECONDS.sleep(100);
        }

        TimeUnit.SECONDS.sleep(6);
        System.out.println("-- 结束模拟测试 --");
        myService.doSomething(2);
    }
}
