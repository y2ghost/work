package study.ywork.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppUserService implements UserDetailsService {
    private static List<UserObject> users = new ArrayList<>();

    public AppUserService() {
        users.add(new UserObject("yy", "123456", "ADMIN"));
        users.add(new UserObject("tt", "123456", "USER"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserObject> user = users.stream().filter(u -> u.name.equals(username)).findAny();
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("没找到用户: " + username);
        }

        return toUserDetails(user.get());
    }

    private UserDetails toUserDetails(UserObject userObject) {
        return User.withUsername(userObject.name).password(userObject.password).roles(userObject.role).build();
    }

    private static class UserObject {
        private String name;
        private String password;
        private String role;

        public UserObject(String name, String password, String role) {
            this.name = name;
            this.password = password;
            this.role = role;
        }
    }
}
