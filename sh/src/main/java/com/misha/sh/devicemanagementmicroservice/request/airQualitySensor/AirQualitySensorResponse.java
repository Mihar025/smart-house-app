package com.misha.sh.devicemanagementmicroservice.request.airQualitySensor;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AirQualitySensorResponse {

    private Integer id;
    private String deviceName;
    private Boolean isActive;
    private Boolean turnOn;
    private Double pm25Level;
    private Double pm10Level;
    private Integer co2Level;
    private Double coLevel;
    private Double no2Level;
    private Double o3Level;
    private Integer vocLevel;
    private Double temperature;
    private Double humidity;
    private Double pressure;
    private Integer airQualityIndex;
    private Integer updateFrequency;
    private Double pm25AlertThreshold;
    private Integer co2AlertThreshold;
    private Boolean hasWifiModule;
    private Double accuracy;
    private LocalDateTime lastCalibrationDate;
}
