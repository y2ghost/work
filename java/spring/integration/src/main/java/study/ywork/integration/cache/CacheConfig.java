package study.ywork.integration.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    // 全局的KEY生成器类注册
    @Override
    public KeyGenerator keyGenerator() {
        return new MyKeyGenerator();
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("employeeCache");
    }
}
