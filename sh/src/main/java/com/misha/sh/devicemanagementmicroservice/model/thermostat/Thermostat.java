package com.misha.sh.devicemanagementmicroservice.model.thermostat;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Thermostat extends Device {

    private double currentTemperature;
    private double targetTemperature;
    private boolean isHeating;
    private boolean isCooling;
    private int humidity;
    private String temperatureMode; // например, "heat", "cool", "auto", "off"
    private boolean autoMode;
    private boolean temporaryMode;

}