package study.ywork.cloud.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.cloud.rest.domain.Hello;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class HelloController {
    private AtomicLong counter = new AtomicLong();
    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    @GetMapping("/hello")
    public Hello getHelloWordObject() {
        Hello hello = new Hello();
        StringBuilder msg = new StringBuilder("Your Number: ");
        msg.append(counter.incrementAndGet());
        msg.append(", Instance ID: ");
        msg.append(instanceId);
        hello.setMessage(msg.toString());
        return hello;
    }
}

