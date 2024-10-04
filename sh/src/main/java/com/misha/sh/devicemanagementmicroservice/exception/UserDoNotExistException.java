package com.misha.sh.devicemanagementmicroservice.exception;

public class UserDoNotExistException  extends RuntimeException{
    public UserDoNotExistException(String message) {
        super(message);
    }
}
