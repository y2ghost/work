package study.ywork.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.web.domain.Employee;

@RestController
public class YamlController {
    @PostMapping(value = "/newEmployee", consumes = "text/yaml", produces = "text/plain;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public String newEmployee(@RequestBody Employee employee) {
        System.out.printf("Employee: %s%n", employee);
        String s = String.format("Employee保存了: %s", employee.getName());
        System.out.println(s);
        return s;
    }

    @GetMapping(value = "/employee", produces = "text/yaml")
    @ResponseStatus(HttpStatus.OK)
    public Employee getEmployee(@RequestParam("id") String id) {
        // 测试用的
        return new Employee(id, "test", "111-111-1111");
    }
}
