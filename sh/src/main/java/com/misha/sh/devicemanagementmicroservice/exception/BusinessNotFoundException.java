package com.misha.sh.devicemanagementmicroservice.exception;

public class BusinessNotFoundException extends RuntimeException {
    public BusinessNotFoundException(String msg) {
        super(msg);
    }
}
