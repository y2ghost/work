package study.ywork.activitiweb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import study.ywork.activitiweb.mapper.UserInfoMapper;
import study.ywork.activitiweb.pojo.UserInfo;

@Component
public class MyUserDetailsService implements UserDetailsService {
    private UserInfoMapper mapper;

    public MyUserDetailsService(UserInfoMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfoBean = mapper.selectByUsername(username);
        if (userInfoBean == null) {
            throw new UsernameNotFoundException("未知用户");
        }

        return userInfoBean;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}