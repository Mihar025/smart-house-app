package com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode;

import com.misha.sh.devicemanagementmicroservice.model.thermostat.TemperatureMode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ThermostatHeatModeResponse {

    private Integer thermostatId;
    private boolean temporaryMode;
    private boolean autoMode;
    private double currentTemperature;
    private double targetTemperature;
    private TemperatureMode temperatureMode;
    private Integer ownerId;

}
