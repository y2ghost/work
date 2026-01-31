package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Controller
@RequestMapping("/upload")
public class UploadController {
    @GetMapping
    public String handleGet() {
        return "file-upload";
    }

    @PostMapping
    public String handlePost(@RequestParam("user-file") MultipartFile multipartFile, Model model) throws IOException {
        String name = multipartFile.getOriginalFilename();
        File tempFile = File.createTempFile("upload-", ".txt");
        System.out.println(tempFile.getAbsolutePath());
        tempFile.deleteOnExit();

        FileWriter fw = new FileWriter(tempFile);
        try (BufferedWriter w = new BufferedWriter(fw)) {
            w.write(new String(multipartFile.getBytes()));
            w.flush();
        }

        model.addAttribute("msg", "文件: " + name);
        return "file-upload-done";
    }
}
