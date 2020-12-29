package study.ywork.springboot.redis;

import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTests {
    private static final Logger LOG = LoggerFactory.getLogger(RedisTests.class);
    /* 通用操作模板 */
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    /* 仅仅操作字符串的模版 */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void opsForValue() {
        redisTemplate.opsForValue().set("name", "yy");
        String name = redisTemplate.opsForValue().get("name");
        LOG.info("name is {}", name);
        // 更新数据
        redisTemplate.opsForValue().set("name", "yy2");
        name = stringRedisTemplate.opsForValue().get("name");
        LOG.info("name is {}", name);
        redisTemplate.delete("name");
    }
}