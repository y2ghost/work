package study.ywork.security.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import study.ywork.security.service.AppUserService;

// WEB安全配置示例
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @SuppressWarnings("unused")
    private DataSource ds;

    public AppSecurityConfig(DataSource ds) {
        super();
        this.ds = ds;
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        // 认证数据源可以来自数据库，示例代码如下:
        // builder.jdbcAuthentication().passwordEncoder(encoder).dataSource(ds)
        // AuthenticationManagerBuilder类的三个方法:
        // jdbcAuthentication 和 inMemoryAuthentication 以及userDetailsService
        // 都是设置内部defaultUserDetailsService对象
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
        // 添加自定义的AuthenticationProvider对象
        builder.authenticationProvider(new AppAuthenticationProvider());
        // 会使builder#inMemoryAuthentication方法配置的数据无效
        // 目的测试自定义的UserDetailsService
        builder.userDetailsService(new AppUserService());
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
            .permitAll()
            .and()
            .exceptionHandling()
            .accessDeniedPage("/order/noaccess"); // 主要是针对订单示例错误处理
    }
}
