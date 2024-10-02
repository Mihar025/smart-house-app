package com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOn;

import com.misha.sh.devicemanagementmicroservice.model.swtichLight.ColorTemperature;

import com.misha.sh.devicemanagementmicroservice.model.swtichLight.LightSwitchMode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SwitchTurnOnResponse {

    private Integer switchId;
    private Boolean isOn;
    private Integer brightness;
    private ColorTemperature colorTemperature;
    private Integer maxWattage;
    private Integer minWattage;
    private Integer ownerId;
    private LightSwitchMode mode;

}
