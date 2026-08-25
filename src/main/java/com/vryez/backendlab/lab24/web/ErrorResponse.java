package com.vryez.backendlab.lab24.web;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<ErrorField> errors;

    public ErrorResponse(String code, String message, List<ErrorField> errors) {

        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public ErrorResponse(String code, String message) {

        this(code, message, List.of());
    }

    @RequiredArgsConstructor
    static class ErrorField{

        private final String field;
        private final String reason;
    }
}
