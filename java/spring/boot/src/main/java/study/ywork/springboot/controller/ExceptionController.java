package study.ywork.springboot.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import study.ywork.springboot.error.ExceptionResponse;
import study.ywork.springboot.exception.TodoNotFoundException;

@ControllerAdvice
@RestController
public class ExceptionController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(TodoNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> todoNotFound(TodoNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(),
            "Any details you would want to add, for test not found exception!!!");
        return new ResponseEntity<>(response, new HttpHeaders(),
            HttpStatus.NOT_FOUND);
    }

}
