package com.vryez.backendlab.lab12.exception;

public class PayoutRejectedException extends RuntimeException {

    public PayoutRejectedException(String message) {
        super(message);
    }
}
