package study.ywork.circuit.breaker.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/*
 * 演示熔断机制的示例
 * 可以通过@HystrixProperty注解指定相关的配置参数
 * 配置文件可以通过: hystrix.command.default.<the-property>配置覆盖默认配置
 * 配置文件可以自定义配置: hystrix.command.<commandKey>.<the-property>
 * 自定义配置配合注解: @HystrixCommand(..., commandKey = "doSomethingKey")使用
 * Hystrix使用ThreadPoolExecutor来处理兵法的请求可以通过如下配置更改并发能力
 * - 使用hystrix.threadpool.default.<propertyName>方法覆盖默认值
 * - 使用@HystrixCommand注解的threadPoolProperties属性进行配置
 * - 使用@HystrixCommand注解的threadPoolKey属性进行自定义配置
 * - 自定义配置示例: hystrix.threadpool.<threadPoolKey>.<propertyName>
 */
@Service
public class MyService {
    // 启动断路器熔断机制后执行指定的fallback函数
    // 默认另开线程执行该方法，如果需要和调用者保持同一个线程，设定隔离策略属性值如下所示
    // @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    @HystrixCommand(fallbackMethod = "fallback", commandProperties = {
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
        @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "500"),
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "1"),
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "1000") })
    public void doSomething(int input) {
        // 如果被除数为0，则会触发异常
        System.out.println("output: " + 10 / input);
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public void doSomething2(int input) {
        try {
            // 模拟超时场景
            TimeUnit.MILLISECONDS.sleep(1500);
        } catch (InterruptedException e) {
            return;
        }

        System.out.println("output: " + 10 / input);
    }

    @SuppressWarnings("unused")
    private void fallback(int input, Throwable throwable) {
        System.out.printf("fallback here, input=%d, exception=%s%n", input, throwable);
    }
}