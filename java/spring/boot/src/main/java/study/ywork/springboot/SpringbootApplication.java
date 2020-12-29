package study.ywork.springboot;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import study.ywork.springboot.client.ToDoRestClient;
import study.ywork.springboot.entity.ToDo;

@SpringBootApplication
public class SpringbootApplication {
    private static final Logger LOG = LoggerFactory.getLogger(SpringbootApplication.class);
    @Autowired
    private String info;
    @Value("${server.port}")
    private String serverPort;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringbootApplication.class);
        app.setBanner((env, cls, out) -> out.print("\n\n\tThis is YY banner!\n\n".toUpperCase()));
        app.run(args);
    }

    @Bean
    String info() {
        return "仅仅是个测试的豆子";
    }

    /*
     * 测试加载命令行参数值 测试方式: java -jar xxx.jar --enable arg1 arg2
     */
    @Bean
    CommandLineRunner yyRun() {
        return args -> {
            LOG.info("## > CommandLineRunner实现示例...");
            LOG.info("## > 参数的个数: {}", args.length);
            LOG.info("## > 发现豆子信息: {}", info);

            for (String arg : args) {
                LOG.info(arg);
            }
        };
    }

    /*
     * 测试客户端REST访问，命令行参数client启动
     * 例子: java -jar xxx.jar --client
     */
    @Bean
    ApplicationRunner restClient() {
        return args -> {
            if (!args.containsOption("client")) {
                return;
            }

            ToDoRestClient client = new ToDoRestClient(serverPort);
            LOG.info("CLIENT > 测试客户端REST访问");
            Iterable<ToDo> toDos = client.findAll();
            assert null != toDos;
            toDos.forEach(toDo -> LOG.info("CLIENT > iter toDo: {}", toDo));

            ToDo newToDo = client.upsert(new ToDo("Drink plenty of Water daily!"));
            assert null != newToDo;
            LOG.info("CLIENT > newToDo: {}", newToDo);

            ToDo toDo = client.findById(newToDo.getId());
            assert null != toDo;
            LOG.info("CLIENT > toDo: {}", toDo);

            ToDo completed = client.setCompleted(newToDo.getId());
            assert completed.isCompleted();
            LOG.info("CLIENT > completed: {}", completed);

            client.delete(newToDo.getId());
            assert null == client.findById(newToDo.getId());
        };
    }
}

/*
 * 测试命令行参数对象注入 测试方式: java -jar xxx.jar --enable arg1 arg2
 */
@Component
class YyComponent {
    private static final Logger LOG = LoggerFactory.getLogger(YyComponent.class);

    @Autowired
    YyComponent(ApplicationArguments cmdArgs) {
        LOG.info("## > 测试命令行参数对象注入!");
        boolean enable = cmdArgs.containsOption("enable");

        if (enable) {
            LOG.info("## > 发现--enable参数!");
        }

        LOG.info("## > 测试打印其他参数...");
        List<String> args = cmdArgs.getNonOptionArgs();

        if (!args.isEmpty()) {
            args.forEach(LOG::info);
        }
    }
}
