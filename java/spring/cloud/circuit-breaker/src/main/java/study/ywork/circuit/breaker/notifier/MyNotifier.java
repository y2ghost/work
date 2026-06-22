package study.ywork.circuit.breaker.notifier;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;

/*
 * 监听Hystrix Event事件信息
 */
public class MyNotifier extends HystrixEventNotifier {
    @Override
    public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
        System.out.printf("-->Event, type=%s, Key=%s%n", eventType, key);
    }
}
