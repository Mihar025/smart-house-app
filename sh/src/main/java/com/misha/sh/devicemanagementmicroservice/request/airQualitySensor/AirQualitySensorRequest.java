package com.misha.sh.devicemanagementmicroservice.request.airQualitySensor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirQualitySensorRequest {

    private String deviceName;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private String serialNumber;
    private String location;
    private Double pm25AlertThreshold;
    private Integer co2AlertThreshold;
    private Double accuracy;
    private Integer updateFrequency;

}