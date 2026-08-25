package com.vryez.backendlab.lab24.web;

import com.vryez.backendlab.lab23.exception.ExceptionResponse;
import com.vryez.backendlab.lab24.exception.CommentsDisabledException;
import com.vryez.backendlab.lab24.exception.VideoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice(basePackages = "com.vryez.backendlab.lab24")
public class ApiExceptionAdvice {

    @ExceptionHandler(CommentsDisabledException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse commentsDisable(CommentsDisabledException e) {

        return ErrorResponse.builder()
                .code("COMMENTS_DISABLED")
                .message(e.getMessage())
                .errors(List.of())
                .build();
    }

    @ExceptionHandler(VideoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse videoNotFount(VideoNotFoundException e) {

        return ErrorResponse.builder()
                .code("VIDEO_NOT_FOUND")
                .message(e.getMessage())
                .errors(List.of())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse malformedBody(HttpMessageNotReadableException e) {

        return ErrorResponse.builder()
                .code("MALFORMED_JSON")
                .message(e.getMessage())
                .errors(List.of())
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse typeMismatch(MethodArgumentTypeMismatchException e) {

        return ErrorResponse.builder()
                .code("TYPE_MISMATCH")
                .message(e.getMessage())
                .errors(List.of())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationFail(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();

        List<ErrorResponse.ErrorField> errors = bindingResult.getFieldErrors().stream()
                .map(error -> new ErrorResponse.ErrorField(error.getField(), error.getDefaultMessage()))
                .toList();

        return ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message(e.getMessage())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse unexpected(Exception e) {
        log.error("unexpected error", e);

        return ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("서버 오류가 발생했습니다")
                .errors(List.of())
                .build();
    }
}

