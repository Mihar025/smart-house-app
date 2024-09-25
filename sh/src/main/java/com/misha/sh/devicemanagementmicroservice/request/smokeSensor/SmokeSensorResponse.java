package com.misha.sh.devicemanagementmicroservice.request.smokeSensor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmokeSensorResponse {

    private Integer smokeSensorId;
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
