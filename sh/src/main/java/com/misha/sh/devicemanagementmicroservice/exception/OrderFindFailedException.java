package com.misha.sh.devicemanagementmicroservice.exception;

public class OrderFindFailedException extends RuntimeException {
    public OrderFindFailedException(String msg) {
        super(msg);
    }
}
