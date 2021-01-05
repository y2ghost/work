package study.ywork.security.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 基本的内存存储用户密码数据示例
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private DataSource ds;

    public AppSecurityConfig(DataSource ds) {
        super();
        this.ds = ds;
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.jdbcAuthentication().passwordEncoder(encoder).dataSource(ds);
//        builder.inMemoryAuthentication()
//            .passwordEncoder(encoder)
//            .withUser("yy")
//            .password(encoder.encode("123456"))
//            .roles("ADMIN")
//            .and()
//            .withUser("tt")
//            .password(encoder.encode("123456"))
//            .roles("USER");
    }

    // 可以自定义HttpSecurity的配置
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/http/user/**")
            .hasRole("USER") // USER 角色可以访问URL: /user/**
            .antMatchers("/http/admin/**")
            .hasRole("ADMIN") // ADMIN 角色可以访问URL: /admin/**
            .antMatchers("/http/guest/**")
            .permitAll() // 任何人可以访问URL: /guest/**
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
