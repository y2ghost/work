package study.ywork.web.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import study.ywork.web.domain.User;

@Service
public class UserService {
    private Map<Long, User> userMap = new HashMap<>();

    public void saveUser(User user) {
        if (null == user.getId()) {
            user.setId((long) (userMap.size() + 1));
        }

        userMap.put(user.getId(), user);
    }
}
