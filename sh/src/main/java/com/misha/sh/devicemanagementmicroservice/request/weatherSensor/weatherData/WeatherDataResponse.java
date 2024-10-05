package com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherDataResponse {

    private Integer sensorId;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double humidity;
    private Double pressure;
    private Double windSpeed;
    private String windDirection;
    private Double precipitation;
    private LocalDateTime lastUpdateTime;
    private Integer ownerId;



}
