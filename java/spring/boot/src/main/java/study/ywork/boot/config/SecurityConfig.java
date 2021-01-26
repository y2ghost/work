package study.ywork.boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// WEB安全配置示例
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        String secret = encoder.encode("123456");
        builder.inMemoryAuthentication()
                .passwordEncoder(encoder)
                .withUser("yy")
                .password(secret)
                .roles("ADMIN")
                .and()
                .withUser("tt")
                .password(secret)
                .roles("USER");
    }

    // 可以自定义HttpSecurity的配置
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated() // 认证过的用户可以使用所有其他URL，不限制角色
                .and()
                .formLogin() // 启用基本表单的身份验证
                .loginPage("/my-login") // 自定义认证页面
                .permitAll() // 登录页面允许任何人使用
                .and()
                .logout()
                .logoutSuccessUrl("/my-login?logout")
                .permitAll();
    }
}
