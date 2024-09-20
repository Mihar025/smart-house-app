package com.misha.sh.devicemanagementmicroservice.exception;

public class EmailorPasswordAlreadyExistException extends RuntimeException {
    public EmailorPasswordAlreadyExistException(String message){
        super(message);
    }
}
