package com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness;

import com.misha.sh.devicemanagementmicroservice.model.swtichLight.ColorTemperature;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColorTemperatureResponse {

    private Integer lightSwitchId;
    private ColorTemperature colorTemperature;
    private Integer ownerId;



}
