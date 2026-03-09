package study.ywork.integration.web.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/*
 * RestTemplate访问REST API示例
 */
public class UserClient {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/integration/users/40",
            String.class);
        System.out.println("-- 响应数据 --");
        System.out.println("状态码: " + responseEntity.getStatusCodeValue());
        System.out.println("HTTP头: " + responseEntity.getHeaders().toSingleValueMap());
        System.out.println("HTTP BODY: " + responseEntity.getBody());
    }
}
