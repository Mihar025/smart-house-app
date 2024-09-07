package com.misha.sh.devicemanagementmicroservice.exception;

public class CartItemRemoveFailedException extends RuntimeException {
    public CartItemRemoveFailedException(String msg) {
        super(msg);
    }
}
