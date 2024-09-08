package com.misha.sh.devicemanagementmicroservice.request.device.devReq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceResponse {

    private Integer id;
    private String deviceName;
    private DeviceType deviceType;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private String serialNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    private DeviceStatus status;
    private String location;
    private boolean isConnected;
    private Double batteryLevel;


    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
    //todo delete maybe this fields
    private String lowEnergyConsumingMode;
    private String highEnergyConsumingMode;
}
