package com.misha.sh.devicemanagementmicroservice.exception;

public class OrderCreatingFailedException extends RuntimeException {
    public OrderCreatingFailedException(String msg) {
        super(msg);
    }
}
