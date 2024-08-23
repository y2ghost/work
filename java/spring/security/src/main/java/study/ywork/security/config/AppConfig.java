package study.ywork.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import study.ywork.security.component.MyAuthenticationProvider;
import study.ywork.security.filter.AuthenticationLoggingFilter;
import study.ywork.security.filter.RequestValidationFilter;

@Configuration
public class AppConfig {
    private final AuthenticationProvider authenticationProvider;

    public AppConfig(MyAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.authenticationProvider(authenticationProvider);
        http.authorizeHttpRequests(c -> c.requestMatchers("/hello-id").permitAll()
                .anyRequest().authenticated());
        // 过滤器全局的，测试的时候开启
        http.addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class);
        http.addFilterAfter(new AuthenticationLoggingFilter(), BasicAuthenticationFilter.class);
        http.userDetailsService(userDetailsService());
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        var user = User.withUsername("yy")
                .password("12345")
                .authorities("read")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
