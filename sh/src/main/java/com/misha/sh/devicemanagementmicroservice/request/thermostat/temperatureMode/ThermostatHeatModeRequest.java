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
public class ThermostatHeatModeRequest {

    @NotNull(message = "Current temperature is required")
    @DecimalMax(value = "80.0", message = "Current temperature must not exceed 80.0°F for heat mode")
    private Double currentTemperature;

    @NotNull(message = "Target temperature is required")
    @DecimalMin(value = "50.0", message = "Target temperature must be at least 50.0°F")
    @DecimalMax(value = "80.0", message = "Target temperature must not exceed 80.0°F")
    private Double targetTemperature;

    private boolean temporaryMode;

    private boolean autoMode;

    @NotNull(message = "Temperature mode is required")
    private TemperatureMode temperatureMode;



}
