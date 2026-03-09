package study.ywork.integration.web.server;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
    @PostMapping(produces = "text/plain;charset=UTF-8")
    public String handleFileUpload(@RequestParam("user-file") MultipartFile multipartFile) throws IOException {
        String name = multipartFile.getOriginalFilename();
        System.out.println("原始文件名称: " + name);
        byte[] bytes = multipartFile.getBytes();
        System.out.println("上传文件内容:\n" + new String(bytes));
        return "文件已上传";
    }
}
