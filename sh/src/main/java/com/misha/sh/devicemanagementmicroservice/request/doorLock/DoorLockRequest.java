package com.misha.sh.devicemanagementmicroservice.request.doorLock;

import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoorLockRequest {


    private String deviceName;
    private DeviceType deviceType;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private String serialNumber;
    private DeviceStatus status;
    private Integer accessCode;
}
