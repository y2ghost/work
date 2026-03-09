package study.ywork.web.opencsv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Employee {
    @CsvBindByName(column = "id", required = true)
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "nameGood", required = true)
    @CsvBindByPosition(position = 1)
    private String name;

    @CsvBindByName(column = "age", required = true)
    @CsvBindByPosition(position = 2)
    private Integer age;

    public Employee(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
