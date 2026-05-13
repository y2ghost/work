package study.ywork.boot.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// WEB安全配置示例
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated() // 认证过的用户可以使用所有其他URL，不限制角色
                .and().formLogin() // 启用基本表单的身份验证
                .loginPage("/my-login") // 自定义认证页面
                .permitAll() // 登录页面允许任何人使用
                .and().logout().logoutSuccessUrl("/my-login?logout").permitAll();
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        String secret = encoder.encode("123456");
        List<UserDetails> users = new ArrayList<>();
        UserDetails user = User.withUsername("yy").password(secret).roles("ADMIN").build();
        users.add(user);
        user = User.withUsername("tt").password(secret).roles("USER").build();
        users.add(user);
        return new InMemoryUserDetailsManager(users);
    }
}
