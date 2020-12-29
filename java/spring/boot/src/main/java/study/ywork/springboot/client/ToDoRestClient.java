package study.ywork.springboot.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import study.ywork.springboot.entity.ToDo;
import study.ywork.springboot.error.ToDoErrorHandler;

public class ToDoRestClient {
    private String serverPort;
    private RestTemplate restTemplate;

    public ToDoRestClient(String serverPort) {
        this.serverPort = serverPort;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new ToDoErrorHandler());
    }

    public Iterable<ToDo> findAll() throws URISyntaxException {
        RequestEntity<Iterable<ToDo>> requestEntity = new RequestEntity<>(HttpMethod.GET,new URI(buildRestUrl()));
        ResponseEntity<Iterable<ToDo>> response = restTemplate.exchange(requestEntity,
            new ParameterizedTypeReference<Iterable<ToDo>>(){});
        
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        return null;
    }

    public ToDo upsert(ToDo toDo) throws URISyntaxException {
        RequestEntity<?> requestEntity = new RequestEntity<>(toDo,HttpMethod.POST,new URI(buildRestUrl()));
        ResponseEntity<?> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<ToDo>(){});

        if (response.getStatusCode() == HttpStatus.CREATED) {
            return restTemplate.getForObject(response.getHeaders().getLocation(), ToDo.class);
        }

        return null;
     }

    public ToDo findById(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        return restTemplate.getForObject(buildRestUrl() + "/{id}", ToDo.class, params);
    }

    public ToDo setCompleted(String id) throws URISyntaxException {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        restTemplate.postForObject(buildRestUrl() + "/{id}?_method=patch", null, ResponseEntity.class, params);
        return findById(id);
    }

    public void delete(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        restTemplate.delete(buildRestUrl() + "/{id}", params);
    }

    private String buildRestUrl() {
        return String.format("http://localhost:%s/api/todo", serverPort);
    }
}
