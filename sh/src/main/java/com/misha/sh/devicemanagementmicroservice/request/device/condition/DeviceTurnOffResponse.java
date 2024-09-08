package com.misha.sh.devicemanagementmicroservice.request.device.condition;

import lombok.*;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTurnOffResponse {

    private Integer deviceId;
    private boolean turnOff;
}
