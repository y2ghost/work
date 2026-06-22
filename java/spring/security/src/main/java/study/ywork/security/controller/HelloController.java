package study.ywork.security.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.security.service.NameService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class HelloController {
    private final Logger log = LoggerFactory.getLogger(HelloController.class);
    private final NameService nameService;

    public HelloController(NameService nameService) {
        this.nameService = nameService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    /**
     * 配置可以访问的原始主机域名
     */
    @PostMapping("/test")
    @CrossOrigin("http://localhost:8080")
    public String test() {
        return "HELLO";
    }

    @GetMapping("/hello-user")
    public String hello(Authentication a) {
        return "Hello, " + a.getName() + "!";
    }

    @GetMapping("/hello-id")
    public String helloId() {
        return "Hello-id!";
    }

    @GetMapping("/hello-key")
    public String helloKey() {
        return "Hello-key!";
    }

    @GetMapping("/bye")
    @Async
    public void goodbye() {
        SecurityContext context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        log.info("bye {}", username);
    }

    @GetMapping("/ciao")
    public String ciao() throws Exception {
        Callable<String> task = () -> {
            SecurityContext context = SecurityContextHolder.getContext();
            return context.getAuthentication().getName();
        };

        ExecutorService e = Executors.newCachedThreadPool();
        try {
            var contextTask = new DelegatingSecurityContextCallable<>(task);
            return "Ciao, " + e.submit(contextTask).get() + "!";
        } finally {
            e.shutdown();
        }
    }

    @PostMapping("/ciao")
    public String postCiao() {
        return "Post Ciao";
    }

    @GetMapping("/hola")
    public String hola() throws Exception {
        Callable<String> task = () -> {
            SecurityContext context = SecurityContextHolder.getContext();
            return context.getAuthentication().getName();
        };

        ExecutorService e = Executors.newCachedThreadPool();
        e = new DelegatingSecurityContextExecutorService(e);
        try {
            return "Hola, " + e.submit(task).get() + "!";
        } finally {
            e.shutdown();
        }
    }

    @GetMapping("/hello-read")
    public String helloRead() {
        return "Hello read permission!";
    }

    @GetMapping("/hello-write")
    public String helloWrite() {
        return "Hello write permission!";
    }

    @GetMapping("/hello-rw")
    public String helloReadOrWrite() {
        return "Hello read or write permission!";
    }

    @GetMapping("/hello-auth-exp")
    public String helloRoleExpression() {
        return "Hello auth expression permission!";
    }

    @GetMapping("/hello-role")
    public String helloRoleAdmin() {
        return "Hello role admin permission!";
    }

    @PostMapping("/hello")
    public String postHello() {
        return "Post Hello!";
    }

    @GetMapping("/secret/names/{name}")
    public List<String> names(@PathVariable String name) {
        return nameService.getSecretNames(name);
    }
}
