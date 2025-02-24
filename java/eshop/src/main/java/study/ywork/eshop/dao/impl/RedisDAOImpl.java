package study.ywork.eshop.dao.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import study.ywork.eshop.dao.RedisDAO;

@Repository("redisDAO")
public class RedisDAOImpl implements RedisDAO {
    private StringRedisTemplate redisTemplate;

    public RedisDAOImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
