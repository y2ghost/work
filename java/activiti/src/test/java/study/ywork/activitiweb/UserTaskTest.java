package study.ywork.activitiweb;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;

@SpringBootTest
class UserTaskTest {
    @Autowired
    private TaskService taskService;

    // 查询我的代办任务
    @Test
    void getTasksByAssignee() {
        assertNotNull(taskService);
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned("bajie").list();
        if (CollectionUtil.isNotEmpty(tasks)) {
            int index = 1;
            for (Task t : tasks) {
                System.out.printf("----第%d条任务信息----%n", index++);
                System.out.println("\t任务ID: " + t.getId());
                System.out.println("\t任务名称: " + t.getName());
                System.out.println("\t任务的创建时间: " + t.getCreateTime());
                System.out.println("\t任务的办理人: " + t.getAssignee());
                System.out.println("\t流程实例ID：" + t.getProcessInstanceId());
                System.out.println("\t执行对象ID: " + t.getExecutionId());
                System.out.println("\t流程定义ID: " + t.getProcessDefinitionId());
            }
        }
    }

    // 查询所有任务
    @Test
    void getTasks() {
        assertNotNull(taskService);
        List<Task> list = taskService.createTaskQuery().list();
        int index = 1;
        for (Task tk : list) {
            System.out.printf("----第%d条任务信息----%n", index++);
            System.out.printf("\t任务ID：%s%n", tk.getId());
            System.out.printf("\t任务名称：%s%n", tk.getName());
            System.out.printf("\t任务受理人：%s%n", tk.getAssignee());
        }
    }

    // 查询流程当前活动的任务
    @Test
    void getActiveTasks() {
        assertNotNull(taskService);
        String processId = "0dacf974-0eb2-11ec-a9fb-d45d64ee0e42";
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processId).active().list();
        int index = 1;
        for (Task tk : tasks) {
            System.out.printf("----第%d条任务信息----%n", index++);
            System.out.printf("\t任务ID：%s%n", tk.getId());
            System.out.printf("\t任务名称：%s%n", tk.getName());
            System.out.printf("\t任务受理人：%s%n", tk.getAssignee());
        }
    }
}
