package com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode;


import com.misha.sh.devicemanagementmicroservice.model.thermostat.TemperatureMode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThermostatCoolingModeResponse {
    private boolean temporaryMode;
    private boolean autoMode;
    private Integer thermostatId;
    private double currentTemperature;
    private double targetTemperature;
    private TemperatureMode temperatureMode;
    private Integer ownerId;
}
