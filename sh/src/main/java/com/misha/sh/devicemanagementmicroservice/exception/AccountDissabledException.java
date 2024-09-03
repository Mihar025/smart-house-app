package com.misha.sh.devicemanagementmicroservice.exception;

public class AccountDissabledException extends RuntimeException {
    public AccountDissabledException(String accountIsDisabled) {
        super(accountIsDisabled);
    }
}
