package com.misha.sh.devicemanagementmicroservice.exception;

public class ProductFindFailedException extends RuntimeException {
    public ProductFindFailedException(String msg) {
        super(msg);
    }
}
