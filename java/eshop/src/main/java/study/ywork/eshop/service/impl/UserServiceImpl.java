package study.ywork.eshop.service.impl;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import study.ywork.eshop.dao.RedisDAO;
import study.ywork.eshop.mapper.UserMapper;
import study.ywork.eshop.model.User;
import study.ywork.eshop.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {
    private UserMapper userMapper;
    private RedisDAO redisDAO;

    public UserServiceImpl(UserMapper userMapper,
                           RedisDAO redisDAO) {
        this.userMapper = userMapper;
        this.redisDAO = redisDAO;
    }

    @Override
    public User findUserInfo() {
        return userMapper.findUserInfo();
    }

    @Override
    public User getCachedUserInfo() {
        redisDAO.set("cached_user_lisi", "{\"name\": \"lisi\", \"age\":28}");
        String userJSON = redisDAO.get("cached_user_lisi");
        JSONObject userJSONObject = JSONObject.parseObject(userJSON);
        User user = new User();
        user.setName(userJSONObject.getString("name"));
        user.setAge(userJSONObject.getInteger("age"));
        return user;
    }
}
