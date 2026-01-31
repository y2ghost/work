package study.ywork.activitiweb;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ProcessInstanceTest {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    // 流程变量名称
    private static final String LEADER = "leader";
    private static final String APPLICANT = "applicant";
    private static final String PROCESS_KEY = "yyTestProcess";

    // 初始化流程实例 - 必须先部署了指定名称的流程
    @Test
    void testProcessInstance() {
        initProcessInstance();
        getAndDelProcessInstances();
    }

    private void initProcessInstance() {
        DeploymentTest deployTest = new DeploymentTest();
        deployTest.setRepositoryService(repositoryService);
        deployTest.initDeploymentBPMN();
        deployTest.initDeploymentBPMN();
        Map<String, Object> vars = new HashMap<>();
        vars.put(APPLICANT, "yy1");
        vars.put(LEADER, "yy2");
        Authentication.setAuthenticatedUserId("yy");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, "001", vars);
        String processInstanceId = processInstance.getProcessInstanceId();
        System.out.printf("流程实例ID：%s%n", processInstanceId);

        // 验证是否历史流程存在
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        assertNotNull(historicProcessInstance);
        System.out.println("----历史流程实例----");
        System.out.printf("开始用户: %s%n", historicProcessInstance.getStartUserId());
        System.out.printf("流程定义ID: %s%n", historicProcessInstance.getProcessDefinitionId());
        System.out.printf("历史流程ID: %s%n", historicProcessInstance.getId());
        System.out.printf("endtime: %s%n", historicProcessInstance.getEndTime());
        System.out.printf("starttime: %s%n", historicProcessInstance.getStartTime());
        System.out.printf("name: %s%n", historicProcessInstance.getName());
        List<HistoricVariableInstance> historicVars = historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(processInstanceId).list();
        historicVars.forEach(var -> System.out.printf("%s = %s%n", var.getVariableName(), var.getValue()));
        System.out.printf("getDescription: %s%n", historicProcessInstance.getDescription());

    }

    // 获取并删除流程实例列表
    private void getAndDelProcessInstances() {
        List<ProcessInstance> processes = runtimeService.createProcessInstanceQuery().processDefinitionKey(PROCESS_KEY)
            .list();
        for (ProcessInstance process : processes) {
            System.out.println("----流程实例----");
            System.out.printf("流程名称: %s%n", process.getName());
            System.out.printf("流程实例ID: %s%n", process.getProcessInstanceId());
            System.out.printf("流程定义ID: %s%n", process.getProcessDefinitionId());
            List<HistoricVariableInstance> historicVars = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(process.getProcessInstanceId()).list();
            historicVars.forEach(var -> System.out.printf("%s = %s%n", var.getVariableName(), var.getValue()));
            System.out.printf("是否结束: %s%n", process.isEnded());
            delProcessInstance(process.getProcessInstanceId());
        }
    }

    // 删除流程实例
    private void delProcessInstance(String processInstanceId) {
        runtimeService.deleteProcessInstance(processInstanceId, "测试删除");
        System.out.printf("删除流程实例: %s%n", processInstanceId);
    }
}
