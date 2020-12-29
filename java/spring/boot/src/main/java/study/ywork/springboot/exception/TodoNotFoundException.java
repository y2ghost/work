package study.ywork.springboot.exception;

public class TodoNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 100001L;

    public TodoNotFoundException(String msg) {
        super(msg);
    }
}
