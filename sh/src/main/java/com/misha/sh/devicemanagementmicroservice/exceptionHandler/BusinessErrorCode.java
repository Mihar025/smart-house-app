package com.misha.sh.devicemanagementmicroservice.exceptionHandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCode {

    O_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code"),
    BUSINESS_ERROR_CODE(300, HttpStatus.BAD_REQUEST, "Current password is incorrect"),
    ACCESS_DINED(301, HttpStatus.BAD_REQUEST, "The new password does not match"),
    NOT_FOUND(302, HttpStatus.FORBIDDEN, "User account is locked"),
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Login and / or password is incorrect");

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCode(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}