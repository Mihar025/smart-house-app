package com.misha.sh.devicemanagementmicroservice.service;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account")
;
    private final String namel;

    EmailTemplateName(String namel) {
        this.namel = namel;
    }
}