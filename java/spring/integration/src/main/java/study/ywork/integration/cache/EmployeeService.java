package study.ywork.integration.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/*
 * @Cacheable注解说明
 * 如果函数没有参数，那么SimpleKey.EMPTY作为键值
 * 如果只有一个函数参数，那么该函数参数作为键值
 * 如果多个函数参数，则所有参数组成的SimpleKey实例作为键值
 * 
 * 可以指定KEY生成器类
 * @CacheConfig(cacheNames = "employeeCache", keyGenerator = "myKeyGenerator")
 */
@Service
@CacheConfig(cacheNames = "employeeCache")
public class EmployeeService {
    // 根据函数参数查询是否缓存，有就缓存获取，没有则调用函数获取
    @Cacheable
    public Employee getEmployeeById(int id) {
        System.out.println("根据id获取Employee实例");
        return new Employee(id, "yy", "IT");
    }

    // 可以使用SPEL表达式自定义键值对象
    @Cacheable(key = "#name")
    public Employee getEmployeeByName(String name) {
        System.out.println("根据name获取Employee实例");
        return new Employee(20, name, "QA");
    }

    // 清除缓存信息
    @CacheEvict
    public void clearEmployeeById(int id) {
        System.out.println("根据ID清除员工信息: " + id);
    }
}
