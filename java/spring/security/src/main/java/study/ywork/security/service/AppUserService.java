package study.ywork.security.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
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
        Optional<UserObject> user = users.stream().filter(u -> u.username.equals(username)).findAny();
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("没找到用户: " + username);
        }

        return toUserDetails(user.get());
    }

    private UserDetails toUserDetails(UserObject userObject) {
        return userObject;
    }

    private static class UserObject implements UserDetails {
        private final String username;
        private final String password;
        private final String authority;

        public UserObject(String username, String password, String authority) {
            this.username = username;
            this.password = password;
            this.authority = authority;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(() -> authority);
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
