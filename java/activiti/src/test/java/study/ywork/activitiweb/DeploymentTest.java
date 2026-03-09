package study.ywork.activitiweb;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DeploymentTest {
    @Autowired
    private RepositoryService repositoryService;
    private static final String PROCESS_KEY = "yyTestProcess";

    void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    // 通过测试代码的BPMN目录下的bpmn文件部署流程
    @Test
    void initDeploymentBPMN() {
        String filename = "BPMN/test.bpmn";
        Deployment deployment = repositoryService.createDeployment().addClasspathResource(filename).name("YY测试部署流程")
            .key(PROCESS_KEY).deploy();
        System.out.println(deployment.getName());
        assertNotNull(deployment);
    }

    // 查询流程部署
    @Test
    void getDeployments() {
        List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : deployments) {
            System.out.println("----部署信息----");
            System.out.printf("\t部署ID: %s%n", deployment.getId());
            System.out.printf("\t部署名称: %s%n", deployment.getName());
            System.out.printf("\t部署时间: %s%n", deployment.getDeploymentTime());
            System.out.printf("\t部署KEY: %s%n", deployment.getKey());
        }

        assertTrue(deployments.size() > 0);
    }
}
