package com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarmSensitivity;

import com.misha.sh.devicemanagementmicroservice.model.smokeSensor.SensorSensitivity;
import lombok.*;

@Data
@Builder
public class AlarmSensitivityResponse {

    private Integer smokeSensorId;
    private Double valueForSensitivity;
    private SensorSensitivity sensitivity;
}
