package study.ywork.activitiweb.controller;

import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.activitiweb.pojo.UserInfo;
import study.ywork.activitiweb.security.SecurityUtil;
import study.ywork.activitiweb.util.ResponseVo;
import study.ywork.activitiweb.util.ResponseCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/processes")
public class ProcessInstanceController {
    private ProcessRuntime processRuntime;
    private SecurityUtil securityUtil;
    private RepositoryService repositoryService;

    public ProcessInstanceController(ProcessRuntime processRuntime, SecurityUtil securityUtil,
        RepositoryService repositoryService) {
        this.processRuntime = processRuntime;
        this.securityUtil = securityUtil;
        this.repositoryService = repositoryService;
    }

    @GetMapping("/")
    public ResponseVo getInstances(@AuthenticationPrincipal UserInfo userInfoBean) {
        Page<ProcessInstance> processInstances = null;
        try {
            processInstances = processRuntime.processInstances(Pageable.of(0, 50));
            List<ProcessInstance> list = processInstances.getContent();
            list.sort((y, x) -> x.getStartDate().toString().compareTo(y.getStartDate().toString()));
            List<HashMap<String, Object>> listMap = new ArrayList<>();

            for (ProcessInstance pi : list) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", pi.getId());
                hashMap.put("name", pi.getName());
                hashMap.put("status", pi.getStatus());
                hashMap.put("processDefinitionId", pi.getProcessDefinitionId());
                hashMap.put("processDefinitionKey", pi.getProcessDefinitionKey());
                hashMap.put("startDate", pi.getStartDate());
                hashMap.put("processDefinitionVersion", pi.getProcessDefinitionVersion());
                ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(pi.getProcessDefinitionId()).singleResult();
                hashMap.put("resourceName", pd.getResourceName());
                hashMap.put("deploymentId", pd.getDeploymentId());
                listMap.add(hashMap);
            }

            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, listMap);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "获取流程实例失败", e.toString());
        }
    }

    @PostMapping("/start")
    public ResponseVo startProcess(@RequestParam String processDefinitionKey, @RequestParam String instanceName,
        @RequestParam String instanceVariable) {
        try {
            securityUtil.loginAs(SecurityContextHolder.getContext().getAuthentication().getName());

            ProcessInstance processInstance = processRuntime
                .start(ProcessPayloadBuilder.start().withProcessDefinitionKey(processDefinitionKey)
                    .withName(instanceName).withBusinessKey("自定义BusinessKey").build());
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS,
                processInstance.getName() + "；" + processInstance.getId());
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "创建流程实例失败", e.toString());
        }
    }

    @PostMapping("/delete")
    public ResponseVo deleteInstance(@RequestParam("instanceID") String instanceID) {
        try {
            ProcessInstance processInstance = processRuntime
                .delete(ProcessPayloadBuilder.delete().withProcessInstanceId(instanceID).build());
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, processInstance.getName());
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "删除流程实例失败", e.toString());
        }

    }

    @PostMapping("/suspend")
    public ResponseVo suspendInstance(@RequestParam("instanceID") String instanceID) {
        try {
            ProcessInstance processInstance = processRuntime
                .suspend(ProcessPayloadBuilder.suspend().withProcessInstanceId(instanceID).build());
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, processInstance.getName());
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "挂起流程实例失败", e.toString());
        }
    }

    @PostMapping("/resume")
    public ResponseVo resumeInstance(@RequestParam("instanceID") String instanceID) {
        try {
            ProcessInstance processInstance = processRuntime
                .resume(ProcessPayloadBuilder.resume().withProcessInstanceId(instanceID).build());
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, processInstance.getName());
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "激活流程实例失败", e.toString());
        }
    }

    @GetMapping(value = "/{instanceID}/variables")
    public ResponseVo variables(@PathVariable String instanceID) {
        try {
            List<VariableInstance> variableInstance = processRuntime
                .variables(ProcessPayloadBuilder.variables().withProcessInstanceId(instanceID).build());
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, variableInstance);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "获取流程参数失败", e.toString());
        }
    }
}
