package study.ywork.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import study.ywork.webflux.handler.HelloHandler;

@SpringBootApplication
@EnableWebFlux
public class App {
    @Bean
    HelloHandler helloHandler() {
        return new HelloHandler();
    }

    @Bean
    RouterFunction<ServerResponse> helloRouterFunction(HelloHandler helloHandler) {
        return RouterFunctions.route(RequestPredicates.path("/"), helloHandler::handleRequest);
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
