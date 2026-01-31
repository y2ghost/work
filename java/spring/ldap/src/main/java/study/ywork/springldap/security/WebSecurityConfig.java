package study.ywork.springldap.security;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.ldap.EmbeddedLdapServerContextSourceFactoryBean;
import org.springframework.security.config.ldap.LdapPasswordComparisonAuthenticationManagerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.ldap.userdetails.PersonContextMapper;
import org.springframework.security.web.SecurityFilterChain;
import study.ywork.springldap.constants.PwdEncodingAlgo;
import study.ywork.springldap.model.LdapAuthStructure;

@Configuration
@EnableWebSecurity
@ComponentScan("study.ywork.springldap.security")
public class WebSecurityConfig {
    @Autowired
    private LdapAuthStructure ldapAuthStructure;
    private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().antMatchers("/js/**");
            web.ignoring().antMatchers("/css/**");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll().anyRequest().fullyAuthenticated().and().formLogin()
                .loginPage("/login").permitAll().defaultSuccessUrl("/privatePage", true).failureUrl("/login?error=true")
                .and().logout().permitAll().logoutSuccessUrl("/login?logout=true");
        logger.info("配置SecurityFilterChain ...");
        return http.build();
    }

    @Bean
    public EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean() {
        EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean = EmbeddedLdapServerContextSourceFactoryBean
                .fromEmbeddedLdapServer();
        contextSourceFactoryBean.setPort(0);
        contextSourceFactoryBean.setManagerDn(ldapAuthStructure.getLdapManagerDn());
        contextSourceFactoryBean.setManagerPassword(ldapAuthStructure.getLdapManagerPwd());
        return contextSourceFactoryBean;
    }

    /**
     * 这种认证配置作用于全局 LDAP认证配置 存在两个基本认证配置工厂类 LdapBindAuthenticationManagerFactory
     * LdapPasswordComparisonAuthenticationManagerFactory
     */
    @Bean
    AuthenticationManager ldapAuthenticationManager(BaseLdapPathContextSource contextSource) {
        LdapPasswordComparisonAuthenticationManagerFactory factory = new LdapPasswordComparisonAuthenticationManagerFactory(
                contextSource, new BCryptPasswordEncoder());
        factory.setUserDetailsContextMapper(new PersonContextMapper());
        factory.setUserDnPatterns(ldapAuthStructure.getUserDnPattern());
        factory.setUserSearchBase(ldapAuthStructure.getUserSearchBase());
        factory.setPasswordAttribute("userPassword");
        logger.info("配置AuthenticationManager ...");
        return factory.createAuthenticationManager();
    }

// JDBC用户认证配置示例
//    @Bean
//    public DataSource dataSource() {
//        return new EmbeddedDatabaseBuilder()
//            .setType(EmbeddedDatabaseType.H2)
//            .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
//            .build();
//    }
//
//    @Bean
//    public UserDetailsManager users(DataSource dataSource) {
//        UserDetails user = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build();
//        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
//        users.createUser(user);
//        return users;
//    }

// 内存用户认证配置示例
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PwdEncodingAlgo.BCRYPT.getStatus(), new BCryptPasswordEncoder());
        encoders.put(PwdEncodingAlgo.PBKDF2.getStatus(), new Pbkdf2PasswordEncoder());
        encoders.put(PwdEncodingAlgo.SCRYPT.getStatus(), new SCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(PwdEncodingAlgo.BCRYPT.getStatus(), encoders);
    }
}
