package study.ywork.webflux;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class MyClient {
    public static void main(String[] args) {
        test1();
        test2();
    }

    private static void test1() {
        WebClient webClient = WebClient.create("http://localhost:8080");
        Mono<String> result = webClient.get().retrieve().bodyToMono(String.class);
        String response = result.block();
        System.out.println(response);
    }

    private static void test2() {
        WebClient webClient = WebClient.create("http://localhost:8080");
        Mono<String> result = webClient.get().retrieve().bodyToMono(String.class);
        result.subscribe(MyClient::handleResponse);
        System.out.println("After subscribe");
        // 需要稍等下，以便任务处理完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void handleResponse(String s) {
        System.out.println("handle response");
        System.out.println(s);
    }
}
