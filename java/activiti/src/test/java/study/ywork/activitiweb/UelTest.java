package study.ywork.activitiweb;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.ywork.activitiweb.security.SecurityUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UEL表达式测试 变量默认范围：流程全周期，本地变量作用范围范围只在当前任务
 */
@SpringBootTest
class UelTest {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private SecurityUtil securityUtil;
    // 发起人
    private static final String APPROVER = "approver";
    // 申请金额
    private static final String MONEY = "money";
    // 流程文件定义的ID
    private static final String PROCESS_KEY = "uelTest";

    // 用来验证实体类变量 - 一般不建议使用
    public static class UelPojo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String approver;

        public String getApprover() {
            return approver;
        }

        public void setApprover(String approver) {
            this.approver = approver;
        }
    }

    // 测试启动流程实例带参数
    @Test
    void testUel() {
        // 首先部署测试流程
        String deployId = deployBPMN();
        // 启动流程带参数
        // 第一个任务使用APPROVER变量
        // 第二个任务使用uelpojo.approver变量并完成任务时设置money变量
        Map<String, Object> startVars = new HashMap<>();

        // 测试MONEY变量此处设置值，后续不能被覆盖，关闭测试设置值为null即可
        Integer startMoney = 5;
        if (null != startMoney) {
            startVars.put(MONEY, startMoney);
        }

        startVars.put(APPROVER, "bajie");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, startVars);
        assertNotNull(processInstance);
        System.out.println("流程实例ID：" + processInstance.getProcessInstanceId());

        // bajie执行第一个任务，并设置下一个任务的执行人wukong
        Map<String, Object> bajieVars = new HashMap<>();
        UelPojo uelPojo = new UelPojo();
        uelPojo.setApprover("wukong");
        bajieVars.put("uelpojo", uelPojo);
        doTasks("bajie", bajieVars);

        // wukong执行第二个任务，并设置变量money变量
        Map<String, Object> wukongVars = new HashMap<>();
        Integer money = new Random().nextInt(200);
        System.out.printf("wukong 申请金额: %d%n", money);
        wukongVars.put(MONEY, money);
        doTasks("wukong", wukongVars);

        // 根据金额判断执行人，如果之前的流程设置MONEY变量，需要使用之前的值进行判断
        Integer fixMoney = money;
        if (null != startMoney) {
            fixMoney = startMoney;
        }

        String nextApprover = null;
        if (fixMoney > 100) {
            nextApprover = "salaboy";
        } else {
            nextApprover = "admin";
        }

        System.out.printf("模拟金额判断: %d，任务转交审批人: %s%n", fixMoney, nextApprover);
        doTasks(nextApprover, null);
        // 最后删除部署
        deleteBPMN(deployId);
    }

    private void doTasks(String assignee, Map<String, Object> vars) {
        System.out.printf("%s 开始处理代办任务%n", assignee);
        List<Task> todoTasks = taskService.createTaskQuery().processDefinitionKey(PROCESS_KEY).taskAssignee(assignee)
            .list();
        if (null == todoTasks || todoTasks.isEmpty()) {
            System.out.printf("%s 没有待办任务%n", assignee);
            return;
        }

        // 测试变量是否会被覆盖
        // 测试只取第一条处理进行处理
        Task todoTask = todoTasks.get(0);
        String taskId = todoTask.getId();
        Map<String, Object> oldVars = taskService.getVariables(taskId);

        if (null != oldVars) {
            Integer oldMoney = (Integer) oldVars.get(MONEY);
            if (null != oldMoney) {
                System.out.printf("\tMONEY变量: %d%n", oldMoney);
            }
        }

        assignee = todoTask.getAssignee();
        securityUtil.loginAs(assignee);
        String taskName = todoTask.getName();
        System.out.printf("\t任务ID: %s%n", taskId);
        System.out.printf("\t任务名称: %s%n", taskName);
        System.out.printf("\t任务处理人: %s%n", assignee);

        if (null != vars) {
            taskService.complete(taskId, vars);
        } else {
            taskService.complete(taskId);
        }

        System.out.printf("%s 完成任务: %s%n", assignee, taskName);
    }

    private String deployBPMN() {
        String deployFile = "BPMN/uel.bpmn";
        Deployment deployment = repositoryService.createDeployment().addClasspathResource(deployFile).name("部署测试UEL的流程")
            .deploy();
        System.out.printf("部署名称: %s, 实例ID: %s%n", deployment.getName(), deployment.getId());
        return deployment.getId();
    }

    private void deleteBPMN(String deployId) {
        repositoryService.deleteDeployment(deployId, true);
        System.out.printf("删除部署: %s%n", deployId);
    }
}
