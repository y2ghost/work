package study.ywork.security.service;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import study.ywork.security.domain.Employee;

import java.util.List;
import java.util.Map;

@Service
public class BookService {
    private Map<String, Employee> records =
            Map.of("yy",
                    new Employee("writer0",
                            List.of("math easy"),
                            List.of("write", "read")),
                    "tt",
                    new Employee("writer1",
                            List.of("Beautiful Paris"),
                            List.of("write"))
            );

    @PostAuthorize("returnObject.roles.contains('read')")
    public Employee getBookDetails(String name) {
        return records.get(name);
    }
}

