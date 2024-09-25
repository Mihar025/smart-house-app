package com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlarmResponse {
    private Integer alarmId;
    private String alarmName;
    private Double SmokeValue;
    private Double alarmThreshold;
    private LocalDateTime lastAlarmTime;
}
