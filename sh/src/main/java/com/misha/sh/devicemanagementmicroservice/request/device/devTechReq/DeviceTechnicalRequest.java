package com.misha.sh.devicemanagementmicroservice.request.device.devTechReq;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTechnicalRequest {



    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
}

