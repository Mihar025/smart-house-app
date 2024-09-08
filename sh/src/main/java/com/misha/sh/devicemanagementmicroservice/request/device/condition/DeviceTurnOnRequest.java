package com.misha.sh.devicemanagementmicroservice.request.device.condition;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTurnOnRequest {
    private boolean turnOn;
}
