package com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat;

import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class ThermostatRequest {

    @NotBlank(message = "Device name is required")
    @Size(max = 100, message = "Device name must be at most 100 characters")
    private String deviceName;

    @NotNull(message = "Device type is required")
    private DeviceType deviceType;

    @Size(max = 500, message = "Device description must be at most 500 characters")
    private String deviceDescription;

    @NotBlank(message = "Manufacturer is required")
    @Size(max = 100, message = "Manufacturer must be at most 100 characters")
    private String manufacturer;

    @NotBlank(message = "Device model is required")
    @Size(max = 100, message = "Device model must be at most 100 characters")
    private String deviceModel;

    @NotNull(message = "Device status is required")
    private DeviceStatus status;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must be at most 200 characters")
    private String location;

    private boolean isConnected;

    @Min(value = 0, message = "Voltage must be a positive number")
    @Max(value = 240, message = "Voltage must not exceed 240")
    private Double voltage;

    @Min(value = 0, message = "Amps must be a positive number")
    private Integer amps;

    @Pattern(regexp = "^\\d+(\\.\\d+)?\\s*(kWh|Wh)$", message = "Energy consumption must be in format '10.5 kWh' or '500 Wh'")
    private String energyConsumingPerHours;

    @NotNull(message = "Owner ID is required")
    @Positive(message = "Owner ID must be a positive number")
    private Integer ownerId;

}