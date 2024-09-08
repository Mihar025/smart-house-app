package com.misha.sh.devicemanagementmicroservice.request.device.condition;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTurnOffRequest {
    private boolean turnOff;
}
