package com.misha.sh.devicemanagementmicroservice.request.device.devTechReq;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTechnicalResponse {

    private Integer deviceId;
    private boolean isActive;
    private Double batteryLevel;
    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
}
