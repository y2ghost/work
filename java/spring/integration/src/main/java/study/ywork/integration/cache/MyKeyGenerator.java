package study.ywork.integration.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

/*
 * 自定义缓存KEY生成器类
 */
@Component("myKeyGenerator")
public class MyKeyGenerator implements KeyGenerator {
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(target.getClass().getSimpleName()).append("-").append(method.getName());

        if (params != null) {
            for (Object param : params) {
                sb.append("-").append(param.getClass().getSimpleName()).append(":").append(param);
            }
        }

        return sb.toString();
    }
}
