package com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat;


import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThermostatResponse {
    private Integer deviceId;
    private String deviceName;
    private DeviceType deviceType;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private DeviceStatus status;
    private String location;
    private boolean isConnected;
    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
    private Integer ownerId;




}
