package study.ywork.activitiweb.pojo;

public class ActRuTask {
    private String name;
    private String taskDefKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    @Override
    public String toString() {
        return "ActRuTask [name=" + name + ", taskDefKey=" + taskDefKey + "]";
    }
}
