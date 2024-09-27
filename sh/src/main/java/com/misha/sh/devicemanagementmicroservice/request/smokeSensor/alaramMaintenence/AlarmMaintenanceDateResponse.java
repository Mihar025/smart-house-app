package com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alaramMaintenence;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlarmMaintenanceDateResponse {
    private Integer smokeSensorId;
    private LocalDateTime lastMaintenanceDate;
}
