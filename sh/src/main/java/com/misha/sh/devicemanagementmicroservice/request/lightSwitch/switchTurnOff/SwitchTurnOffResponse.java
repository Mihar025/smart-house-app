package com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOff;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SwitchTurnOffResponse {

    private Integer switchId;
    private boolean isOn;
    private Integer ownerId;
}
