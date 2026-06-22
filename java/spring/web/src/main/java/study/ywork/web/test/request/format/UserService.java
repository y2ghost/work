package study.ywork.web.test.request.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<Long, User> userMap = new HashMap<>();

    public void saveUser(User user) {
        if (user.getId() == null) {
            user.setId((long) (userMap.size() + 1));
        }
        userMap.put(user.getId(), user);

    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }
}
