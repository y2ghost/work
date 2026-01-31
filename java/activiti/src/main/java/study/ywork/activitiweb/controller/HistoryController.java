package study.ywork.activitiweb.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.activitiweb.pojo.UserInfo;
import study.ywork.activitiweb.util.ResponseVo;
import study.ywork.activitiweb.util.ResponseCode;

@RestController
@RequestMapping("/historic")
public class HistoryController {
    private RepositoryService repositoryService;
    private HistoryService historyService;

    public HistoryController(RepositoryService repositoryService,
        HistoryService historyService) {
        this.repositoryService = repositoryService;
        this.historyService = historyService;
    }

    // 用户历史任务
    @GetMapping("/tasks")
    public ResponseVo getTaskInstances(@RequestParam String assignee,
        @RequestParam String processId) {
        try {
            HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc();
            if (null != assignee) {
                taskQuery.taskAssignee(assignee);
            }

            if (null != processId) {
                taskQuery.processInstanceId(processId);
            }

            List<HistoricTaskInstance> historicTaskInstances = taskQuery.list();
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, historicTaskInstances);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "获取历史任务失败", e.toString());
        }
    }

    // 流程图高亮
    @GetMapping("/processes/{processId}/flow-chart")
    public ResponseVo getFlowChart(@PathVariable String instanceId,
        @AuthenticationPrincipal UserInfo userInfo) {
        try {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(instanceId).singleResult();
            // 获取bpmnModel对象
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            // 因为我们这里只定义了一个Process 所以获取集合中的第一个即可
            Process process = bpmnModel.getProcesses().get(0);
            // 获取所有的FlowElement信息
            Collection<FlowElement> flowElements = process.getFlowElements();

            Map<String, String> map = new HashMap<>();
            for (FlowElement flowElement : flowElements) {
                // 判断是否是连线
                if (flowElement instanceof SequenceFlow) {
                    SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                    String ref = sequenceFlow.getSourceRef();
                    String targetRef = sequenceFlow.getTargetRef();
                    map.put(ref + targetRef, sequenceFlow.getId());
                }
            }

            // 获取流程实例全部历史节点
            List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(instanceId).list();
            // 各个历史节点 两两组合 key
            Set<String> keyList = new HashSet<>();
            for (HistoricActivityInstance i : list) {
                for (HistoricActivityInstance j : list) {
                    if (i != j) {
                        keyList.add(i.getActivityId() + j.getActivityId());
                    }
                }
            }
            // 高亮连线ID
            Set<String> highLine = new HashSet<>();
            keyList.forEach(s -> highLine.add(map.get(s)));

            // 获取流程实例 历史节点（已完成）
            List<HistoricActivityInstance> listFinished = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(instanceId).finished().list();
            // 高亮节点ID
            Set<String> highPoint = new HashSet<>();
            listFinished.forEach(s -> highPoint.add(s.getActivityId()));

            // 获取流程实例 历史节点（待办节点）
            List<HistoricActivityInstance> listUnFinished = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(instanceId).unfinished().list();

            // 需要移除的高亮连线
            Set<String> set = new HashSet<>();
            // 待办高亮节点
            Set<String> waitingToDo = new HashSet<>();
            listUnFinished.forEach(s -> {
                waitingToDo.add(s.getActivityId());
                for (FlowElement flowElement : flowElements) {
                    if (flowElement instanceof UserTask) {
                        UserTask userTask = (UserTask) flowElement;
                        if (userTask.getId().equals(s.getActivityId())) {
                            List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
                            // 把高亮待办节点之后即出的连线去掉
                            if (outgoingFlows != null && !outgoingFlows.isEmpty()) {
                                outgoingFlows.forEach(a -> {
                                    if (a.getSourceRef().equals(s.getActivityId())) {
                                        set.add(a.getId());
                                    }
                                });
                            }
                        }
                    }
                }
            });

            highLine.removeAll(set);
            // 存放高亮我的办理节点
            Set<String> iDo = new HashSet<>();
            // 当前用户已完成的任务
            String assigneeName = userInfo.getUsername();
            List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(assigneeName).finished().processInstanceId(instanceId).list();
            taskInstanceList.forEach(a -> iDo.add(a.getTaskDefinitionKey()));
            Map<String, Object> reMap = new HashMap<>();
            reMap.put("highPoint", highPoint);
            reMap.put("highLine", highLine);
            reMap.put("waitingToDo", waitingToDo);
            reMap.put("iDo", iDo);
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, reMap);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "渲染历史流程失败", e.toString());
        }
    }
}
