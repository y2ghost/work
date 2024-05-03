package study.ywork.integration.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;

public class CacheUtils {
    public static void printNativeCache(ApplicationContext context) {
        Cache cache = context.getBean(CacheManager.class).getCache("employeeCache");
        System.out.println("-- 本地缓存信息 --");
        System.out.println(cache.getNativeCache());
    }

    private CacheUtils() {
    }
}
