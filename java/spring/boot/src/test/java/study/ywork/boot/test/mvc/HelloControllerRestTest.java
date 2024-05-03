package study.ywork.boot.test.mvc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

/*
 * 使用WebTestClient测试类示例
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerRestTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int port;

    @Test
    public void useWebTestClient() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/body")
                .queryParam("name", "yy")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("你好 yy");
    }

    @Test
    public void useTestRestTemplate() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/body?name={nameValue}",
            String.class, "yy");
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("你好 yy");
    }

    @Test
    public void useServerPort() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "http://localhost:{portValue}/body?name={nameValue}",
            String.class, port, "yy");
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("你好 yy");
    }
}
