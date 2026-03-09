package study.ywork.security.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import study.ywork.security.component.CustomAuthenticationProvider;
import study.ywork.security.component.DocumentsPermissionEvaluator;
import study.ywork.security.component.MyAuthenticationProvider;
import study.ywork.security.filter.AuthenticationLoggingFilter;
import study.ywork.security.filter.CsrfTokenLoggerFilter;
import study.ywork.security.filter.MyHeaderFilter;
import study.ywork.security.filter.RequestValidationFilter;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableAsync
@EnableMethodSecurity(
        jsr250Enabled = true,
        securedEnabled = true
)
public class SecurityConfig {
    private PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;
    private final MyHeaderFilter headerFilter;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final CsrfTokenRepository csrfTokenRepository;
    private final DocumentsPermissionEvaluator evaluator;


    public SecurityConfig(PasswordEncoder passwordEncoder,
                          UserDetailsService userDetailsService,
                          DataSource dataSource,
                          MyHeaderFilter headerFilter,
                          AuthenticationSuccessHandler authenticationSuccessHandler,
                          AuthenticationFailureHandler authenticationFailureHandler,
                          CsrfTokenRepository csrfTokenRepository,
                          DocumentsPermissionEvaluator evaluator) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.dataSource = dataSource;
        this.headerFilter = headerFilter;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.csrfTokenRepository = csrfTokenRepository;
        this.evaluator = evaluator;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.cors(c -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("*"));
                config.setAllowedMethods(List.of("*"));
                config.setAllowedHeaders(List.of("*"));
                return config;
            };
            c.configurationSource(source);
        });
        http.csrf(c -> {
            c.csrfTokenRepository(csrfTokenRepository);
            c.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
            /* 忽略CSRF处理1 */
            // c.ignoringRequestMatchers("/ciao");
            /* 忽略CSRF处理2 */
            // HandlerMappingIntrospector i = new HandlerMappingIntrospector();
            // MvcRequestMatcher r = new MvcRequestMatcher(i, "/ciao");
            // c.ignoringRequestMatchers(r);
            /* 忽略CSRF处理3 */
            // String pattern = ".*[0-9].*";
            // String httpMethod = HttpMethod.POST.name();
            // RegexRequestMatcher r = new RegexRequestMatcher(pattern, httpMethod);
            // c.ignoringRequestMatchers(r);
        });
        http.httpBasic(c -> c.realmName("DEMO").authenticationEntryPoint(new CustomEntryPoint()));
        http.formLogin(c -> c.successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
        );
        String roleExpression = "hasAuthority('read') and !hasAuthority('delete')";
        http.authorizeHttpRequests(c -> c.requestMatchers("/hello-id").permitAll()
                .requestMatchers("/hello-key").permitAll()
                .requestMatchers("/my-error").permitAll()
                .requestMatchers("/hello-read").hasAuthority("read")
                .requestMatchers("/hello-write").hasAuthority("write")
                .requestMatchers("/hello-rw").hasAnyAuthority("read", "write")
                .requestMatchers("/hello-auth-exp").access(new WebExpressionAuthorizationManager(roleExpression))
                .requestMatchers("/hello-role-admin").hasRole("ADMIN")
                .anyRequest().authenticated());
        // 过滤器全局的，测试的时候开启
        http.addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class);
        http.addFilterAfter(new AuthenticationLoggingFilter(), BasicAuthenticationFilter.class);
        http.addFilterAt(headerFilter, BasicAuthenticationFilter.class);
        http.addFilterAfter(new CsrfTokenLoggerFilter(), CsrfFilter.class);
        http.userDetailsService(userDetailsService);
        // 可以多个认证类，每个认证类可以绑定单独的userDetailsService
        http.authenticationProvider(new MyAuthenticationProvider());
        http.authenticationProvider(new CustomAuthenticationProvider(h2UserDetailsService(), passwordEncoder));
        http.authenticationProvider(new CustomAuthenticationProvider(memUserDetailsService(), passwordEncoder));
        return http.build();
    }

    @Bean
    InitializingBean initializingBean() {
        // 允许框架管理的线程可以拷贝安全上下文，非框架管理的线程不会生效
        return () -> SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public MethodSecurityExpressionHandler createExpressionHandler() {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(evaluator);
        return expressionHandler;
    }

    private UserDetailsService h2UserDetailsService() {
        String usersByUsernameQuery = "select username, password, enabled from demo.users where username = ?";
        String authsByUserQuery = "select username, authority from demo.authorities where username = ?";
        var userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
        userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);
        return userDetailsManager;
    }

    private UserDetailsService memUserDetailsService() {
        var uds = new InMemoryUserDetailsManager();
        uds.createUser(User.withUsername("test")
                .passwordEncoder(passwordEncoder::encode)
                .password("123456")
                .authorities("read", "write", "delete")
                .build());
        uds.createUser(User.withUsername("test_read")
                .passwordEncoder(passwordEncoder::encode)
                .password("123456")
                .authorities("read")
                .build());
        uds.createUser(User.withUsername("test_write")
                .passwordEncoder(passwordEncoder::encode)
                .password("123456")
                .authorities("write")
                .build());
        uds.createUser(User.withUsername("test_role")
                .passwordEncoder(passwordEncoder::encode)
                .password("123456")
                .roles("ADMIN")
                .build());
        return uds;
    }
}
