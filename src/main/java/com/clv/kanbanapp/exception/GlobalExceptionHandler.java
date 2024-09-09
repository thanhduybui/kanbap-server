package com.clv.kanbanapp.exception;


import com.clv.kanbanapp.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String INVALID_ARGUMENT = "Invalid argument";
    private static final String INTERNAL_SERVER_ERROR = "Internal server error";
    private static final String NOT_FOUND = "We don't have this route";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseData> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getDefaultMessage());
        }
        log.error("Validation error: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseData.builder()
                        .status(com.clv.kanbanapp.service.ResponseStatus.ERROR.toString())
                        .message(errors.get(0)).build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseData> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        // You can customize the response as needed
        return ResponseEntity.badRequest().body(
                ResponseData.builder()
                        .status(com.clv.kanbanapp.service.ResponseStatus.ERROR.toString())
                        .message(ex.getMessage()).build());
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseData> handleValidationException(ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.builder()
                            .status(com.clv.kanbanapp.service.ResponseStatus.ERROR.toString())
                            .message(ex.getMessage()).build());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ResponseData> handleMethodArgumentTypeMismatch(Exception ex) {
        return ResponseEntity.badRequest().body(
                ResponseData.builder()
                        .status(com.clv.kanbanapp.service.ResponseStatus.ERROR.toString())
                        .message(INVALID_ARGUMENT).build());
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseData> handleInternalServerError(Exception ex) {
        return ResponseEntity.badRequest().body(
                ResponseData.builder()
                        .status(com.clv.kanbanapp.service.ResponseStatus.ERROR.toString())
                        .message(INTERNAL_SERVER_ERROR).build());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseData> handleNotFound(NoHandlerFoundException ex) {
        log.error("No handler found for request: {}", ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseData.builder()
                        .status(com.clv.kanbanapp.service.ResponseStatus.ERROR.toString())
                        .message(NOT_FOUND).build());
    }
}
