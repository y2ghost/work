package study.ywork.activitiweb.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.util.Collection;
import static org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId;

@Component
public class SecurityUtil {
    private Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    private UserDetailsService userDetailsService;

    public SecurityUtil(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void loginAs(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (user == null) {
            throw new IllegalStateException("User " + username + " doesn't exist, please provide a valid user");
        }

        logger.info("使用{}身份登陆", username);
        SecurityContextHolder.setContext(new SecurityContextImpl(new Authentication() {
            private static final long serialVersionUID = 1L;

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return user.getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return user.getPassword();
            }

            @Override
            public Object getDetails() {
                return user;
            }

            @Override
            public Object getPrincipal() {
                return user;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                // 留空不处理
            }

            @Override
            public String getName() {
                return user.getUsername();
            }
        }));
        setAuthenticatedUserId(username);
    }
}
