package study.ywork.activitiweb.controller;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.activitiweb.mapper.ActivitiMapper;
import study.ywork.activitiweb.pojo.ActRuTask;
import study.ywork.activitiweb.pojo.UserInfo;
import study.ywork.activitiweb.security.SecurityUtil;
import study.ywork.activitiweb.util.ResponseVo;
import study.ywork.activitiweb.util.ResponseCode;
import java.util.List;

@RestController
@RequestMapping("/hello")
public class HelloController {
    private TaskRuntime taskRuntime;
    private SecurityUtil securityUtil;
    ActivitiMapper mapper;
    private ProcessRuntime processRuntime;

    public HelloController(TaskRuntime taskRuntime,
        SecurityUtil securityUtil,
        ActivitiMapper mapper,
        ProcessRuntime processRuntime) {
        this.taskRuntime = taskRuntime;
        this.securityUtil = securityUtil;
        this.mapper = mapper;
        this.processRuntime = processRuntime;
    }

    @GetMapping("/")
    public String say() {
        return "hello activiti";
    }

    @GetMapping("/me1")
    public Object getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/me2")
    public Object user(@AuthenticationPrincipal UserInfo userInfo) {
        return userInfo.getName();
    }

    @GetMapping("/tasks")
    public Object getTasks() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        securityUtil.loginAs(userName);

        try {
            Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
            return "任务信息: " + tasks.getTotalItems();
        } catch (Exception e) {
            return "错误：" + e;
        }
    }

    @GetMapping("/tasks/db")
    public Object getdb() {
        try {
            List<ActRuTask> tasks = mapper.selectName();
            return tasks.toString();
        } catch (Exception e) {
            return "错误：" + e;
        }
    }

    @GetMapping("/processes/start")
    public ResponseVo testStartProcess(@RequestParam String processDefinitionKey,
        @RequestParam String instanceName) {
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            securityUtil.loginAs(userName);
            ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start()
                .withProcessDefinitionKey(processDefinitionKey)
                .withName(instanceName)
                .withVariable("assignee", userName)
                .withVariable("day", "4")
                .withBusinessKey("自定义BusinessKey")
                .build());
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS,
                processInstance.getName() + "；" + processInstance.getId());
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "创建流程实例失败", e.toString());
        }
    }

    @GetMapping(value = "/tasks/finish")
    public ResponseVo testcompleteTask(@RequestParam String taskID) {
        try {
            Task task = taskRuntime.task(taskID);
            taskRuntime.complete(TaskPayloadBuilder.complete()
                .withTaskId(task.getId())
                .withVariable("day", "2")
                .build());
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, "成功完成");
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "完成失败", e.toString());
        }
    }
}
