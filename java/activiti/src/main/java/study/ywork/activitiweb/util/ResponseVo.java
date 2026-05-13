package study.ywork.activitiweb.util;

public class ResponseVo {
    private Integer status;
    private String msg;
    private Object obj;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    private ResponseVo(Integer status, String msg, Object obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }

    public static ResponseVo newResponseVo(ResponseCode code, Object obj) {
        return new ResponseVo(code.getCode(), code.getDesc(), obj);
    }

    public static ResponseVo newResponseVo(ResponseCode code, String msg, Object obj) {
        return new ResponseVo(code.getCode(), msg, obj);
    }
}
