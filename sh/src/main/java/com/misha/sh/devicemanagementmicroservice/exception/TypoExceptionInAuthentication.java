package com.misha.sh.devicemanagementmicroservice.exception;

public class TypoExceptionInAuthentication extends RuntimeException{

    public TypoExceptionInAuthentication(String message){
        super(message);
    }

}
