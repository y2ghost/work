package study.ywork.springboot.mongo;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import study.ywork.springboot.dao.MongoUserRepository;
import study.ywork.springboot.entity.MongoUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTests {
    @Autowired
    private MongoUserRepository userRepository;

    @Before
    public void init() {
        userRepository.save(new MongoUser("1", "Robert", "robert@gmail.com"));
        userRepository.save(new MongoUser("2", "Dan", "dan@gmail.com"));
    }

    @Test
    public void findAllUsers() {
        List<MongoUser> users = userRepository.findAll();
        assertNotNull(users);
        assertTrue(!users.isEmpty());
    }

    @Test
    public void findUserById() {
        MongoUser user = userRepository.findById("1").get();
        assertNotNull(user);
    }

    @Test
    public void createUser() {
        MongoUser user = new MongoUser("3", "Joseph", "joseph@gmail.com");
        MongoUser savedUser = userRepository.save(user);
        MongoUser newUser = userRepository.findById(savedUser.getId()).get();
        assertEquals("Joseph", newUser.getName());
        assertEquals("joseph@gmail.com", newUser.getEmail());

    }

    @Test
    public void findUserByName() {
        MongoUser user = userRepository.findByUserName("Robert");
        assertNotNull(user);
    }
}