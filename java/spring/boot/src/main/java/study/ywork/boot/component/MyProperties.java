package study.ywork.boot.component;

import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.Max;

/*
 * @Validated是只用Spring Validator校验机制使用
 * @Valid是使用Hibernate validation的时候使用
 * 需要注意这样的细节区别，不过@Valid注解可以用于类内部的属性
 * 表示进行级联验证
 */
@Validated
@Component
@ConfigurationProperties("my")
public class MyProperties {
    private String name;
    @Max(18)
    private Integer age;
    private List<String> adminEmails;
    private LocalDate birthDate;
    private String password;

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

    public List<String> getAdminEmails() {
        return adminEmails;
    }

    public void setAdminEmails(List<String> adminEmails) {
        this.adminEmails = adminEmails;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "MyProperties [name=" + name + ", age=" + age + ", adminEmails=" + adminEmails + ", birthDate="
            + birthDate + ", password=" + password + "]";
    }
}
