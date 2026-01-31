package study.ywork.activitiweb.controller;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.*;
import study.ywork.activitiweb.mapper.ActivitiMapper;
import study.ywork.activitiweb.security.SecurityUtil;
import study.ywork.activitiweb.util.ResponseVo;
import study.ywork.activitiweb.util.ResponseCode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private TaskRuntime taskRuntime;
    private ProcessRuntime processRuntime;
    private RepositoryService repositoryService;
    ActivitiMapper mapper;

    public TaskController(TaskRuntime taskRuntime, SecurityUtil securityUtil, ProcessRuntime processRuntime,
        RepositoryService repositoryService, ActivitiMapper mapper) {
        this.taskRuntime = taskRuntime;
        this.repositoryService = repositoryService;
        this.mapper = mapper;
    }

    @GetMapping("/todo")
    public ResponseVo getTasks() {
        try {
            Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
            List<HashMap<String, Object>> listMap = new ArrayList<>();

            for (Task tk : tasks.getContent()) {
                ProcessInstance processInstance = processRuntime.processInstance(tk.getProcessInstanceId());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", tk.getId());
                hashMap.put("name", tk.getName());
                hashMap.put("status", tk.getStatus());
                hashMap.put("createdDate", tk.getCreatedDate());
                if (tk.getAssignee() == null) {
                    hashMap.put("assignee", "待拾取任务");
                } else {
                    hashMap.put("assignee", tk.getAssignee());//
                }

                hashMap.put("instanceName", processInstance.getName());
                listMap.add(hashMap);
            }

            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, listMap);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "获取我的代办任务失败", e.toString());
        }
    }

    @PostMapping("/{taskID}/complete")
    public ResponseVo completeTask(@PathVariable String taskID) {
        try {
            Task task = taskRuntime.task(taskID);
            if (task.getAssignee() == null) {
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
            }

            taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, taskID);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "完成失败", e.toString());
        }
    }

    @GetMapping(value = "/{taskID}/show-form-data")
    public ResponseVo formDataShow(@PathVariable String taskID) {
        try {
            Task task = taskRuntime.task(taskID);
            HashMap<String, String> controlistMap = new HashMap<>();
            // 本实例保存所有的表单数据
            List<HashMap<String, Object>> tempControlList = mapper.selectFormData(task.getProcessInstanceId());

            for (HashMap<String, Object> ls : tempControlList) {
                controlistMap.put(ls.get("Control_ID_").toString(), ls.get("Control_VALUE_").toString());
            }

            UserTask userTask = (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId())
                .getFlowElement(task.getFormKey());
            if (userTask == null) {
                return ResponseVo.newResponseVo(ResponseCode.SUCCESS, "无表单");
            }

            List<FormProperty> formProperties = userTask.getFormProperties();
            List<HashMap<String, Object>> listMap = new ArrayList<>();

            for (FormProperty fp : formProperties) {
                String[] splitFP = fp.getId().split("-_!");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", splitFP[0]);
                hashMap.put("controlType", splitFP[1]);
                hashMap.put("controlLable", splitFP[2]);

                // 默认值如果是表单控件ID
                if (splitFP[3].startsWith("FormProperty_")) {
                    if (controlistMap.containsKey(splitFP[3])) {
                        hashMap.put("controlDefValue", controlistMap.get(splitFP[3]));
                    } else {
                        hashMap.put("controlDefValue", "读取失败，检查" + splitFP[0] + "配置");
                    }
                } else {
                    hashMap.put("controlDefValue", splitFP[3]);
                }

                hashMap.put("controlIsParam", splitFP[4]);
                listMap.add(hashMap);
            }

            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, listMap);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "失败", e.toString());
        }
    }

    // 保存表单
    @PostMapping("/{taskID}/save-form-data")
    public ResponseVo formDataSave(@PathVariable String taskID, @RequestParam("formData") String formData) {
        try {

            Task task = taskRuntime.task(taskID);
            HashMap<String, Object> variables = new HashMap<>();
            Boolean hasVariables = false;
            List<HashMap<String, Object>> listMap = new ArrayList<>();
            // 前端传来的字符串，拆分成每个控件
            String[] formDataList = formData.split("!_!");

            for (String controlItem : formDataList) {
                String[] formDataItem = controlItem.split("-_!");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("PROC_DEF_ID_", task.getProcessDefinitionId());
                hashMap.put("PROC_INST_ID_", task.getProcessInstanceId());
                hashMap.put("FORM_KEY_", task.getFormKey());
                hashMap.put("Control_ID_", formDataItem[0]);
                hashMap.put("Control_VALUE_", formDataItem[1]);
                listMap.add(hashMap);

                switch (formDataItem[2]) {
                case "f":
                    System.out.println("控件值不作为参数");
                    break;
                case "s":
                    variables.put(formDataItem[0], formDataItem[1]);
                    hasVariables = true;
                    break;
                case "t":
                    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    variables.put(formDataItem[0], timeFormat.parse(formDataItem[2]));
                    hasVariables = true;
                    break;
                case "b":
                    variables.put(formDataItem[0], BooleanUtils.toBoolean(formDataItem[2]));
                    hasVariables = true;
                    break;
                default:
                    System.out.println("控件参数类型配置错误：" + formDataItem[0] + "的参数类型不存在，" + formDataItem[2]);
                }
            }

            if (Boolean.TRUE.equals(hasVariables)) {
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskID).withVariables(variables).build());
            } else {
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskID).build());
            }

            // 写入数据库
            mapper.insertFormData(listMap);
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, listMap);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "失败", e.toString());
        }
    }
}
