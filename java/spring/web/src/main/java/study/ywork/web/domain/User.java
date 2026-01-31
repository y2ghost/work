package study.ywork.web.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class User {
    private Long id;

    @Size(min = 5, max = 20, message = "{user.name.size}")
    private String name;

    @Size(min = 6, max = 15, message = "{user.password.size}")
    @Pattern(regexp = "\\S+", message = "{user.password.space}")
    private String password;

    @NotEmpty(message = "{user.email.empty}")
    @Email(message = "{user.email.invalid}")
    private String emailAddress;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", password=" + password + ", emailAddress=" + emailAddress + "]";
    }
}
