package com.misha.sh.devicemanagementmicroservice.service.emailService;

import lombok.Getter;

@Getter
public enum EmailAlarmTemplate {

    ACTIVATE_ALARM("activate_alarm")
    ;
    private final String alarm;

    EmailAlarmTemplate(String alarm) {
        this.alarm = alarm;
    }
}
