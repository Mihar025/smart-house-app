package com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor;

import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import com.misha.sh.devicemanagementmicroservice.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherSensorResponse {


    private Integer id;
    private String deviceName;
    private DeviceType deviceType;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private boolean isActive;
    private boolean isConnected;

    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private DeviceStatus status;
    private Integer ownerId;




}
