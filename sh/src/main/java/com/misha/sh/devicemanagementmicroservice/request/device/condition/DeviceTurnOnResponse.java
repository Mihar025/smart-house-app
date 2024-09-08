package com.misha.sh.devicemanagementmicroservice.request.device.condition;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTurnOnResponse {

    private Integer deviceId;
    private boolean turnOn;
}
