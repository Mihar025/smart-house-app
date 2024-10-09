package com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherSensorEnergyRequest {
    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
}
