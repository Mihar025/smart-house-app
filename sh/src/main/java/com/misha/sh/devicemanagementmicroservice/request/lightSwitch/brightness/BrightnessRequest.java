package com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness;

import com.misha.sh.devicemanagementmicroservice.model.swtichLight.ColorTemperature;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrightnessRequest {


    private Integer brightness;
    private ColorTemperature colorTemperature;
    private Integer ownerId;
}
