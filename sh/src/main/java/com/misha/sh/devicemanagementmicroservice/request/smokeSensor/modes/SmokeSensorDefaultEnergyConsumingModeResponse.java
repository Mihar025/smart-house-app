package com.misha.sh.devicemanagementmicroservice.request.smokeSensor.modes;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SmokeSensorDefaultEnergyConsumingModeResponse {
    private Integer smokeSensorId;
    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
}
