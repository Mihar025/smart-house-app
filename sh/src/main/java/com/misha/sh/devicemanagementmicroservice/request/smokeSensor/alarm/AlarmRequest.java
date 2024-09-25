package com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlarmRequest {
    @NotNull(message = "Value for Smoke Threshold cannot be null")
    @NotEmpty(message = "Smoke Level cannot be empty")
    private Double smokeLevel;
    @NotNull(message = "Value for smoke Threshold cannot be null")
    @NotEmpty(message = "Smoke Threshold cannot be empty")
    private Double smokeThreshold;

}
