package com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode;


import com.misha.sh.devicemanagementmicroservice.model.thermostat.TemperatureMode;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThermostatCoolingModeRequest {
    @NotNull(message = "Current temperature is required")
    @DecimalMin(value = "68.0", message = "Current temperature must be at least 68.0°F")
    @DecimalMax(value = "100.0", message = "Current temperature must not exceed 100.0°F")
    private Double currentTemperature;

    @NotNull(message = "Target temperature is required")
    @DecimalMin(value = "69.0", message = "Target temperature must be at least 69.0°F")
    @DecimalMax(value = "100.0", message = "Target temperature must not exceed 100.0°F")
    private Double targetTemperature;



    private boolean temporaryMode;
    private boolean autoMode;
    @NotNull(message = "Temperature mode is required")
    private TemperatureMode temperatureMode;



}
