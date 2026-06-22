package study.ywork.web.test.request.attribute;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<Long, User> userMap = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        List<String> roles = Arrays.asList("admin", "real-only", "guest");
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setId(i);
            user.setName("test" + i);
            user.setRole(roles.get(i % 3));
            userMap.put((long) i, user);
        }
    }

    public User getUserById(long id) {
        return userMap.get(id);
    }
}
