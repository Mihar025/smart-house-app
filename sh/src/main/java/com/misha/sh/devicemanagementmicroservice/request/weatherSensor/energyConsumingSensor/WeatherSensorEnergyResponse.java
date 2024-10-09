package com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherSensorEnergyResponse {
    private Integer sensorId;
    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
    private Integer ownerId;
}
