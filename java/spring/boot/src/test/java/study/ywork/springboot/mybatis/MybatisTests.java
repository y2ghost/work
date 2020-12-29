package study.ywork.springboot.mybatis;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import study.ywork.springboot.entity.MybatisUser;
import study.ywork.springboot.dao.UserMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void findAllUsers() {
        List<MybatisUser> users = userMapper.findAllUsers();
        assertNotNull(users);
        assertTrue(!users.isEmpty());
    }

    @Test
    public void findUserById() {
        MybatisUser user = userMapper.findUserById(1);
        assertNotNull(user);
    }

    @Test
    public void createUser() {
        MybatisUser user = new MybatisUser(0, "george", "george@gmail.com");
        userMapper.insertUser(user);
        MybatisUser newUser = userMapper.findUserById(user.getId());
        assertEquals("george", newUser.getName());
        assertEquals("george@gmail.com", newUser.getEmail());
    }
}