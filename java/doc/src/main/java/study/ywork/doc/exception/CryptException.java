package study.ywork.doc.exception;

public class CryptException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    public CryptException(String message) {
        super("保护处理异常: " + message);
    }
}
