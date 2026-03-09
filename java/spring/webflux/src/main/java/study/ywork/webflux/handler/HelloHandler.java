package study.ywork.webflux.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class HelloHandler {
    public Mono<ServerResponse> handleRequest(ServerRequest serverRequest) {
        return ServerResponse.ok().body(Mono.just("Hello World From HelloHandler!"), String.class);
    }
}
