package com.vryez.backendlab.lab23.exception;

import com.vryez.backendlab.lab23.VideoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice(basePackages = "com.vryez.backendlab.lab23")
public class VideoGlobalExceptionHandler {

    @ExceptionHandler(VideoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse videoNotFound(VideoNotFoundException e) {
        return ExceptionResponse.of("VIDEO_NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse validationFail(MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return new ExceptionResponse("VALIDATION_ERROR", "입력값이 올바르지 않습니다.", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse malformedBody(HttpMessageNotReadableException e) {
        return ExceptionResponse.of("MALFORMED_REQUEST", "요청 본문을 읽을 수 없습니다.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse typeMismatch(MethodArgumentTypeMismatchException e) {
        return ExceptionResponse.of("TYPE_MISMATCH", "파라미터 형식이 올바르지 않습니다.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse unexpected(Exception e) {
        log.error("unexpected error", e);
        return ExceptionResponse.of("INTERNAL_ERROR", "서버 오류가 발생했습니다.");
    }
}
