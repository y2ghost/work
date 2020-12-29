package study.ywork.springldap.security;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import study.ywork.springldap.constants.PwdEncodingAlgo;
import study.ywork.springldap.model.LdapAuthStructure;

@Configuration
@EnableWebSecurity
@ComponentScan("study.ywork.springldap.security")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private LdapAuthStructure ldapAuthStructure;
    private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**");
        web.ignoring().antMatchers("/css/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/")
            .permitAll()
            .anyRequest()
            .fullyAuthenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .defaultSuccessUrl("/privatePage", true)
            .failureUrl("/login?error=true")
            .and()
            .logout()
            .permitAll()
            .logoutSuccessUrl("/login?logout=true");
        logger.info("configure method is called to make the resources secure ...");
        super.configure(http);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.ldapAuthentication()
            .userDnPatterns(ldapAuthStructure.getUserDnPattern())
            .userSearchBase(ldapAuthStructure.getUserSearchBase())
            .contextSource()
            .url(ldapAuthStructure.getLdapUrl() + "/" + ldapAuthStructure.getLdapBase())
            .managerDn(ldapAuthStructure.getLdapManagerDn())
            .managerPassword(ldapAuthStructure.getLdapManagerPwd())
            .and()
            .passwordCompare()
            .passwordEncoder(new BCryptPasswordEncoder())
            .passwordAttribute("userPassword");
        logger.info("configure method is called to build Authentication manager ...");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PwdEncodingAlgo.BCRYPT.getStatus(), new BCryptPasswordEncoder());
        encoders.put(PwdEncodingAlgo.PBKDF2.getStatus(), new Pbkdf2PasswordEncoder());
        encoders.put(PwdEncodingAlgo.SCRYPT.getStatus(), new SCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(PwdEncodingAlgo.BCRYPT.getStatus(), encoders);
    }
}
