package study.ywork.boot.test.mvc;

import org.springframework.stereotype.Service;

@Service
public class DefaultServiceImpl implements HelloService {
    public String getMessage(String name) {
        return String.format("你好 %s", name);
    }
}
