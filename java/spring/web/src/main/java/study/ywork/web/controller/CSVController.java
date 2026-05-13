package study.ywork.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.web.domain.Employee;
import study.ywork.web.domain.EmployeeList;

import java.util.Arrays;
import java.util.List;

@RestController
public class CSVController {
    @PostMapping(value = "/newEmployee", consumes = "text/csv", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String newEmployee(@RequestBody EmployeeList employeeList) {
        System.out.printf("添加Employee: %s%n", employeeList.getList());
        String s = String.format("size: %d", employeeList.getList().size());
        System.out.println(s);
        return s;
    }

    @GetMapping(value = "/employeeList", produces = "text/csv")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeList getEmployeeList() {
        List<Employee> list = Arrays.asList(new Employee("1", "yy", "111-111-1111"),
                new Employee("2", "tt", "222-222-2222"));
        EmployeeList employeeList = new EmployeeList();
        employeeList.setList(list);
        return employeeList;
    }
}
