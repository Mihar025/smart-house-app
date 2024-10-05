package com.misha.sh.devicemanagementmicroservice.exception;

public class UserUpdatingFailedException extends RuntimeException {
    public UserUpdatingFailedException(String msg) {
        super(msg);
    }
}
