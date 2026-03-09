package study.ywork.web.test.response.body;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<Long, User> userMap = new LinkedHashMap<>();

    public void saveUser(User user) {
        if (null == user.getId()) {
            user.setId((long) (userMap.size() + 1));
        }

        userMap.put(user.getId(), user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }
}
