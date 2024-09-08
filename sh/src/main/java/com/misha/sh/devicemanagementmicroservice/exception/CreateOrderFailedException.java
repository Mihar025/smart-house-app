package com.misha.sh.devicemanagementmicroservice.exception;

public class CreateOrderFailedException extends RuntimeException {
    public CreateOrderFailedException(String msg) {
        super(msg);
    }
}
