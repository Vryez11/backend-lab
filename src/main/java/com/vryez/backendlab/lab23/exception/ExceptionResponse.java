package com.vryez.backendlab.lab23.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ExceptionResponse {

    private final String code;

    private final String message;

    private final List<FieldError> fieldErrors;

    // 필드 오류가 없는 케이스용 — fieldErrors 키는 항상 존재, 기본 빈 배열
    public static ExceptionResponse of(String code, String message) {
        return new ExceptionResponse(code, message, List.of());
    }
}
