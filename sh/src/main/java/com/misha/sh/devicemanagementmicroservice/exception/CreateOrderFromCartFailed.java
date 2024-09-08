package com.misha.sh.devicemanagementmicroservice.exception;

public class CreateOrderFromCartFailed extends RuntimeException {
    public CreateOrderFromCartFailed(String message) {
        super(message);
    }
}
