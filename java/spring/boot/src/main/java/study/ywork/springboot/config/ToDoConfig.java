package study.ywork.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import com.mongodb.MongoClient;

@Configuration
public class ToDoConfig {
    private Environment env;

    public ToDoConfig(Environment env) {
        this.env = env;
    }

    /*
     * 嵌入式mongodb启动，会设置随机的端口值到环境变量中local.mongo.port
     * 只要依赖添加runtimeOnly 'de.flapdoodle.embed:de.flapdoodle.embed.mongo'
     * 需要注解该方法为 @Bean @DependsOn("embeddedMongoServer")
     */
    public MongoClient mongoClient() {
        int port = this.env.getProperty("local.mongo.port", Integer.class);
        return new MongoClient("localhost", port);
    }
}
