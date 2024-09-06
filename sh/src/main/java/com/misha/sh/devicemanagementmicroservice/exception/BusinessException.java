package com.misha.sh.devicemanagementmicroservice.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
