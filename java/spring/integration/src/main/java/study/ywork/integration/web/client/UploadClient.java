package study.ywork.integration.web.client;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * RestTemplate上传示例
 */
public class UploadClient {
    public static void main(String[] args) throws IOException {
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("user-file", getUserFileResource());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/integration/upload",
            HttpMethod.POST, requestEntity, String.class);
        System.out.println("response status: " + response.getStatusCode());
        System.out.println("response body: " + response.getBody());
    }

    public static Resource getUserFileResource() throws IOException {
        Path tempFile = Files.createTempFile("upload-test-file", ".txt");
        Files.write(tempFile, "测试上传文件的内容...\n第一行\n第二行".getBytes());
        System.out.println("uploading: " + tempFile);
        File file = tempFile.toFile();
        return new FileSystemResource(file);
    }
}
