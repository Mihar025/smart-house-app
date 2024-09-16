package com.misha.sh.devicemanagementmicroservice.request.smartOutlet;

import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmartOutletResponse {

    private Integer deviceId;
    private String deviceName;
    private DeviceType deviceType;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private String serialNumber;
    private DeviceStatus status;
    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
    private Integer ownerId;





}
