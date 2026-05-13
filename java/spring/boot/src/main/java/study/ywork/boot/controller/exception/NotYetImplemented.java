package study.ywork.boot.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class NotYetImplemented extends Exception {
    private static final long serialVersionUID = 1L;

    public NotYetImplemented(String message) {
        super(message);
    }
}
