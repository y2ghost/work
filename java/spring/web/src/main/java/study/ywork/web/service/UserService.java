package study.ywork.web.service;

import org.springframework.stereotype.Service;
import study.ywork.web.domain.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Long, User> userMap = new HashMap<>();

    public void saveUser(User user) {
        if (null == user.getId()) {
            user.setId((long) (userMap.size() + 1));
        }

        userMap.put(user.getId(), user);
    }
}
