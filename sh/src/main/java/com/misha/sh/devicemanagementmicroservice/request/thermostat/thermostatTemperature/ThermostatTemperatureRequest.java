package com.misha.sh.devicemanagementmicroservice.request.thermostat.thermostatTemperature;

import com.misha.sh.devicemanagementmicroservice.model.thermostat.TemperatureMode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThermostatTemperatureRequest {

    private double currentTemperature;
    private double targetTemperature;
    private Boolean isHeating;
    private Boolean isCooling;
    private TemperatureMode temperatureMode;
    private Boolean autoMode;
    private Boolean temporaryMode;

}
