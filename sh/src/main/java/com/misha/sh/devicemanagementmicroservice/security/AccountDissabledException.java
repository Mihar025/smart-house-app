package com.misha.sh.devicemanagementmicroservice.security;


public class AccountDissabledException extends RuntimeException {
    public AccountDissabledException(String msg) {
        super(msg);
    }
}
