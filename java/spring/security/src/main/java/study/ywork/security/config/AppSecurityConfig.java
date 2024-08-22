package study.ywork.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import study.ywork.security.service.AppUserService;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

// WEB安全配置示例
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class AppSecurityConfig {
    private static final String DEFAULT_PASSWORD = "123456";
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @SuppressWarnings("unused")
    private final DataSource ds;

    public AppSecurityConfig(DataSource ds) {
        super();
        this.ds = ds;
    }

    // 内存用户认证配置示例
    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        String secret = encoder.encode(DEFAULT_PASSWORD);
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
        http.authorizeHttpRequests(c -> c.requestMatchers("/http/user/**").hasRole("USER") // USER 角色可以访问URL: /user/**
                        .requestMatchers("/http/admin/**").hasRole("ADMIN") // ADMIN 角色可以访问URL: /admin/**
                        .requestMatchers("/http/guest/**").permitAll() // 任何人可以访问URL: /guest/**
                        .anyRequest().authenticated() // 认证过的用户可以使用所有其他URL，不限制角色
                )
                .formLogin(f -> f.loginPage("/my-login").permitAll())
                .logout(l -> l.logoutSuccessUrl("/my-login?logout").permitAll())
                .exceptionHandling(e -> e.accessDeniedPage("/order/noaccess"))
                .userDetailsService(userDetailsService())
                .rememberMe(r -> r.rememberMeCookieName("app-known-me")
                        .tokenValiditySeconds(600)
                        .userDetailsService(userDetailsService()));
        return http.build();
    }
}
