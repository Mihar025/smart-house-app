package com.misha.sh.devicemanagementmicroservice.exception;

public class OperationNotPermittedException extends RuntimeException {
    public OperationNotPermittedException(String message) {
        super(message);

    }
}
