package study.ywork.security.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import javax.sql.DataSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import study.ywork.security.service.AppUserService;

// WEB安全配置示例
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class AppSecurityConfig {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @SuppressWarnings("unused")
    private DataSource ds;

    public AppSecurityConfig(DataSource ds) {
        super();
        this.ds = ds;
    }

    // 内存用户认证配置示例
    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        String secret = encoder.encode("123456");
        List<UserDetails> users = new ArrayList<>();
        UserDetails user = User.withUsername("yy").password(secret).roles("ADMIN").build();
        users.add(user);
        user = User.withUsername("tt").password(secret).roles("USER").build();
        users.add(user);
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AppAuthenticationProvider();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new AppUserService();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/http/user/**").hasRole("USER") // USER 角色可以访问URL: /user/**
                .antMatchers("/http/admin/**").hasRole("ADMIN") // ADMIN 角色可以访问URL: /admin/**
                .antMatchers("/http/guest/**").permitAll() // 任何人可以访问URL: /guest/**
                .anyRequest().authenticated() // 认证过的用户可以使用所有其他URL，不限制角色
                .and().formLogin() // 启用基本表单的身份验证
                .loginPage("/my-login") // 自定义认证页面
                .permitAll() // 登录页面允许任何人使用
                .and().logout().logoutSuccessUrl("/my-login?logout").permitAll().and().exceptionHandling()
                .accessDeniedPage("/order/noaccess") // 主要是针对订单示例错误处理
                .and().rememberMe() // 使用RememberMeService服务
                .rememberMeCookieName("app-known-me").tokenValiditySeconds(600);
        return http.build();
    }
}
