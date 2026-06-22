package study.ywork.activitiweb.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;

public class PiListener implements ExecutionListener {
    private static final long serialVersionUID = 1L;
    private Expression sendType;

    public PiListener(Expression sendType) {
        this.sendType = sendType;
    }

    @Override
    public void notify(DelegateExecution execution) {
        System.out.println(execution.getEventName());
        System.out.println(execution.getProcessDefinitionId());
        if ("start".equals(execution.getEventName())) {
            System.out.println("记录节点开始时间");
        } else if ("end".equals(execution.getEventName())) {
            System.out.println("记录节点结束时间");
        }

        System.out.println("sendType:" + sendType.getValue(execution).toString());
    }
}
