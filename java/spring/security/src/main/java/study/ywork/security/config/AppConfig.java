package study.ywork.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import study.ywork.security.component.MyAuthenticationProvider;
import study.ywork.security.filter.AuthenticationLoggingFilter;
import study.ywork.security.filter.MyHeaderFilter;
import study.ywork.security.filter.RequestValidationFilter;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    private final UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;
    private final DataSource dataSource;
    private final MyHeaderFilter headerFilter;

    public AppConfig(UserDetailsService userDetailsService,
                     AuthenticationProvider authenticationProvider,
                     DataSource dataSource,
                     MyHeaderFilter headerFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationProvider = authenticationProvider;
        this.dataSource = dataSource;
        this.headerFilter = headerFilter;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.authorizeHttpRequests(c -> c.requestMatchers("/hello-id").permitAll()
                .requestMatchers("/hello-key").permitAll()
                .anyRequest().authenticated());
        // 过滤器全局的，测试的时候开启
        http.addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class);
        http.addFilterAfter(new AuthenticationLoggingFilter(), BasicAuthenticationFilter.class);
        http.addFilterAt(headerFilter, BasicAuthenticationFilter.class);
        // 用户数据可以来自多个，最后一个设置的一般作为默认值
        http.userDetailsService(h2DetailsService());
        http.userDetailsService(userDetailsService);
        // 可以多个认证类
        http.authenticationProvider(new MyAuthenticationProvider());
        http.authenticationProvider(authenticationProvider);
        return http.build();
    }

    private UserDetailsService h2DetailsService() {
        String usersByUsernameQuery = "select username, password, enabled from demo.users where username = ?";
        String authsByUserQuery = "select username, authority from demo.authorities where username = ?";
        var userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
        userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);
        return userDetailsManager;
    }
}
