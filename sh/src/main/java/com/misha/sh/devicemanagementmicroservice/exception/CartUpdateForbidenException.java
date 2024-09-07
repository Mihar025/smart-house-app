package com.misha.sh.devicemanagementmicroservice.exception;

public class CartUpdateForbidenException extends RuntimeException{
    public CartUpdateForbidenException(String msg) {
        super(msg);
    }
}
