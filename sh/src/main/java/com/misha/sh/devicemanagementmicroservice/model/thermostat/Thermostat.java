package com.misha.sh.devicemanagementmicroservice.model.thermostat;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Thermostat extends Device {

    private double currentTemperature;
    private double targetTemperature;
    private Boolean isHeating;
    private Boolean isCooling;
    private int humidity;

    @Enumerated(EnumType.STRING)
    private TemperatureMode temperatureMode; // например, "heat", "cool", "auto", "off"

    private Boolean autoMode;
    private Boolean temporaryMode;

}