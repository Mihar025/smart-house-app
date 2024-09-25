package com.misha.sh.devicemanagementmicroservice.request.smokeSensor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmokeSensorRequest {

    private String deviceName;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private String serialNumber;
}
