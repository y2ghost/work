package study.ywork.activitiweb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 角色信息配置示例
 * 本类添加@Configuration注解
 * 方法添加@Bean注解即可生效
 */
public class ExampleConfiguration {
    private Logger logger = LoggerFactory.getLogger(ExampleConfiguration.class);
    private static final String ROLE_ACTIVITI_USER = "ROLE_ACTIVITI_USER";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String ACTIVITI_TREAM = "GROUP_activitiTeam";

    public UserDetailsService myUserDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        String[][] usersGroupsAndRoles = {
            { "zhangsan", DEFAULT_PASSWORD, ROLE_ACTIVITI_USER, ACTIVITI_TREAM },
            { "lisi", DEFAULT_PASSWORD, ROLE_ACTIVITI_USER, ACTIVITI_TREAM },
            { "wangwu", DEFAULT_PASSWORD, ROLE_ACTIVITI_USER, ACTIVITI_TREAM },
            { "test", DEFAULT_PASSWORD, ROLE_ACTIVITI_USER, "GROUP_testTeam" },
            { "admin", DEFAULT_PASSWORD, "ROLE_ACTIVITI_ADMIN", "GROUP_AdminTeam"},
        };

        for (String[] userAuthInfo : usersGroupsAndRoles) {
            List<String> authoritiesStrings = Arrays.asList(Arrays.copyOfRange(userAuthInfo, 2, userAuthInfo.length));
            logger.info("注册新用户: {}, 权限: {}", userAuthInfo[0], authoritiesStrings);
            inMemoryUserDetailsManager.createUser(new User(userAuthInfo[0], passwordEncoder().encode(userAuthInfo[1]),
                authoritiesStrings.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
        }

        return inMemoryUserDetailsManager;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
