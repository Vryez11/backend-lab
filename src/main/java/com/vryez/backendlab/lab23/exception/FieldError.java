package com.vryez.backendlab.lab23.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FieldError {

    private final String field;

    private final String reason;
}
