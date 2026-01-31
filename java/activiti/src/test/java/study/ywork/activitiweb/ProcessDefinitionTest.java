package study.ywork.activitiweb;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

@SpringBootTest
class ProcessDefinitionTest {
    @Autowired
    private RepositoryService repositoryService;
    private static final String PROCESS_KEY = "yyTestProcess";

    // 测试查询和删除流程定义
    @Test
    void getAndDelDefinitions() {
        DeploymentTest deployTest = new DeploymentTest();
        deployTest.setRepositoryService(repositoryService);
        deployTest.initDeploymentBPMN();
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(PROCESS_KEY).list();
        System.out.printf("查询KEY: %s的流程定义列表%n", PROCESS_KEY);

        for (ProcessDefinition definition : processDefinitions) {
            System.out.println("----流程定义----");
            System.out.printf("\t定义ID: %s%n", definition.getId());
            System.out.printf("\t名称: %s%n", definition.getName());
            System.out.printf("\tKEY: %s%n", definition.getKey());
            System.out.printf("\t资源名称: %s%n", definition.getResourceName());
            System.out.printf("\t部署ID: %s%n", definition.getDeploymentId());
            System.out.printf("\t版本: %s%n", definition.getVersion());
            delDefinition(definition.getDeploymentId());
        }

        assertTrue(processDefinitions.size() > 0);
    }

    // 删除流程定义
    private void delDefinition(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        System.out.printf("删除流程定义成功: %s%n", deploymentId);
    }
}
