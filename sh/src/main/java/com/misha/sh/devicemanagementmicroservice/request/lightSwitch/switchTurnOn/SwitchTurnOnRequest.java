package com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOn;

import com.misha.sh.devicemanagementmicroservice.model.swtichLight.ColorTemperature;
import com.misha.sh.devicemanagementmicroservice.model.swtichLight.LightSwitchMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SwitchTurnOnRequest {


    @Min(value = 0, message = "Brightness must be at least 0")
    @Max(value = 100, message = "Brightness must not exceed 100")
    private Integer brightness;

    private ColorTemperature colorTemperature;

    @Min(value = 0, message = "Max wattage must be at least 0")
    private Integer maxWattage;

    @Min(value = 0, message = "Min wattage must be at least 0")
    private Integer minWattage;

    private LightSwitchMode mode;

}
