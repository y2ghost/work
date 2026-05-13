package study.ywork.eshop.service;

import study.ywork.eshop.model.User;

public interface UserService {
    User findUserInfo();

    User getCachedUserInfo();
}
