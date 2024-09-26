package com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarmSensitivity;
import com.misha.sh.devicemanagementmicroservice.model.smokeSensor.SensorSensitivity;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;
@Data
@Builder
public class AlarmSensitivityRequest {

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    @Digits(integer = 1, fraction = 2)
    private Double valueForSensitivity;
    private SensorSensitivity sensitivity;
}
