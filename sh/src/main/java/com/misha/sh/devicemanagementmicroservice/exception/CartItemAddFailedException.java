package com.misha.sh.devicemanagementmicroservice.exception;

public class CartItemAddFailedException extends RuntimeException {
    public CartItemAddFailedException(String msg) {
        super(msg);
    }
}
